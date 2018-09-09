package com.training.dr.androidtraining.data.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.ulils.ApiUtils;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiPutService extends IntentService {

    private static final String TAG = ApiPutService.class.getSimpleName();
    public static final String RATE_BOOK = "RATE_BOOK";
    private static final String RATING_PARAM = "review[rating]";

    public ApiPutService() {
        super(ApiPutService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        String url = intent.getStringExtra("url");
        String method = intent.getStringExtra("method");
        int rate = intent.getIntExtra("rate", 0);
        int id = intent.getIntExtra("id", 0);

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(method)) {
            if (TextUtils.equals(method, RATE_BOOK)) {
                rateBook(url, rate, id);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private void rateBook(String requestUrl, int rate, int id) {
        try {
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("id", id);
            postDataParams.put(RATING_PARAM, rate);

            GoodreadApi api = GoodreadApi.getInstance();
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            api.getoAuthConsumer().sign(conn);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(ApiUtils.getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                insertRating(id, rate);
                updateBookRating(id, rate);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void insertRating(int id, int rating) {
        ContentValues values = new ContentValues();
        values.put(DataBaseUtils.RATING_BOOK_ID, id);
        values.put(DataBaseUtils.RATING_RATE, rating);
        values.put(DataBaseUtils.RATING_RATE_TIMESTAMP, System.currentTimeMillis());
        getContentResolver().insert(
                DataBaseUtils.RATINGS_URI,
                values
        );
    }

    private void updateBookRating(int id, int rating) {
        Uri uri = ContentUris.withAppendedId(DataBaseUtils.BOOK_URI, getBookLocalId(id));
        ContentValues values = new ContentValues();
        values.put(DataBaseUtils.BOOK_MY_RATING, rating);
        getContentResolver().update(
                uri,
                values,
                null,
                null
        );
    }

    private int getBookLocalId(int reviewId) {
        Cursor cursor = getContentResolver().query(DataBaseUtils.BOOK_URI, null,
                DataBaseUtils.BOOK_REVIEW_ID + " = " + reviewId,
                null,
                null);
        if (cursor == null) {
            return 0;
        }
        cursor.moveToFirst();
        int bookLocalId = cursor.getInt(0);
        cursor.close();
        return bookLocalId;
    }
}