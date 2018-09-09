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
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiDeleteService extends IntentService {
    private static final String TAG = ApiDeleteService.class.getSimpleName();
    public static final String DELETE_BOOK = "DELETE_BOOK";

    public ApiDeleteService() {
        super(ApiDeleteService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        String url = intent.getStringExtra("url");
        String method = intent.getStringExtra("method");
        int id = intent.getIntExtra("id", 0);

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(method)) {
            if (TextUtils.equals(method, DELETE_BOOK)) {
                deleteBook(url, id);
            }
        }
        Log.d(TAG, "Service Stopping!");
    }

    private void deleteBook(String requestUrl, int id) {
        try {
            GoodreadApi api = GoodreadApi.getInstance();
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            api.getoAuthConsumer().sign(conn);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                deleteBook(id);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void deleteBook(int id) {
        Uri uri = ContentUris.withAppendedId(DataBaseUtils.BOOK_URI, getBookLocalId(id));
        getContentResolver().delete(uri, null, null);
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