package com.training.dr.androidtraining.data.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.data.models.User;
import com.training.dr.androidtraining.data.parsing.XmlParsingManager;
import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.Utils;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class ApiGetService extends IntentService {

    private static final String TAG = ApiGetService.class.getSimpleName();

    public ApiGetService() {
        super(ApiGetService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");
        String url = intent.getStringExtra("url");

        if (!TextUtils.isEmpty(url)) {
            if (!getData(url)) {
                Intent i = new Intent(Utils.BROADCAST_ACTION);
                i.putExtra(Utils.PARAM_STATUS, Utils.STATUS_ERROR);
                sendBroadcast(i);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private boolean getData(String requestUrl) {
        InputStream inputStream = null;
        try {
            HttpURLConnection urlConnection;
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            GoodreadApi api = GoodreadApi.getInstance();
            if (!requestUrl.contains(ApiMethods.USER_BOOKS_BY_ID)) {
                api.getoAuthConsumer().sign(urlConnection);
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                if (requestUrl.contains(ApiMethods.USER_BOOKS_BY_ID)) {
                    insertBookList(response);
                }
                if (requestUrl.contains(ApiMethods.CURRENT_LOGGED_IN_USER)) {
                    getCurrentUser(response);
                }
                if (requestUrl.contains(ApiMethods.USER_INFO_BY_ID)) {
                    saveUser(response);
                }
                return true;
            }
        } catch (IOException
                | OAuthExpectationFailedException
                | OAuthMessageSignerException
                | OAuthCommunicationException e) {
            Log.e(TAG, e.getMessage());
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    Log.e(TAG, e1.getMessage());
                }
            }
        }
        return false;
    }

    private void getCurrentUser(String response) {
        User currentUser = new XmlParsingManager().parseCurrentUserInfo(response);
        String url = BuildConfig.BASE_URL + ApiMethods.USER_INFO_BY_ID + currentUser.getGoodreadId()
                + ".xml?key=" + BuildConfig.GOODREAD_API_KEY;
        getData(url);
    }

    private void saveUser(String response) {
        User user = new XmlParsingManager().parseUserInfo(response);
        SharedPreferences.Editor editor = SPreferences
                .getInstance()
                .getSharedPreferences()
                .edit();
        editor.putInt(DataBaseUtils.USER_GOODREAD_ID, user.getGoodreadId());
        editor.putString(DataBaseUtils.USER_NAME, user.getName());
        editor.putString(DataBaseUtils.USER_AVATAR_URL, user.getAvatarUrl());
        editor.apply();

        Intent i = new Intent(Utils.BROADCAST_ACTION);
        i.putExtra(Utils.PARAM_STATUS, Utils.STATUS_USER_INIT_FINISH);
        sendBroadcast(i);
    }

    private void insertBookList(String response) {
        List<Item> results = new XmlParsingManager().parseUserBookList(response);
        if (results.size() == 0) {
            Intent i = new Intent(Utils.BROADCAST_ACTION);
            i.putExtra(Utils.PARAM_STATUS, Utils.STATUS_EMPTY_DATA);
            sendBroadcast(i);
            return;
        }
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Item item : results) {
            Book book = (Book) item;
            ops.add(ContentProviderOperation.newInsert(DataBaseUtils.BOOK_URI)
                    .withValue(DataBaseUtils.BOOK_ID, book.getGoodreadId())
                    .withValue(DataBaseUtils.BOOK_GOODREAD_ID, book.getGoodreadId())
                    .withValue(DataBaseUtils.BOOK_TITLE, book.getTitle())
                    .withValue(DataBaseUtils.BOOK_YEAR, book.getYear())
                    .withValue(DataBaseUtils.BOOK_IMAGE_URL, book.getImageUrl())
                    .withValue(DataBaseUtils.BOOK_DESCRIPTION, book.getDescription())
                    .withValue(DataBaseUtils.BOOK_RATING, book.getRating())
                    .withValue(DataBaseUtils.BOOK_AUTHOR, book.getAuthor())
                    .withValue(DataBaseUtils.BOOK_CREATED, book.getCreated())
                    .withValue(DataBaseUtils.BOOK_REVIEW_ID, book.getReviewId())
                    .withValue(DataBaseUtils.BOOK_MY_RATING, book.getMyRating())
                    .withYieldAllowed(true)
                    .build());
        }
        try {
            getContentResolver().applyBatch(DataBaseUtils.AUTHORITY_BOOKS, ops);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

}