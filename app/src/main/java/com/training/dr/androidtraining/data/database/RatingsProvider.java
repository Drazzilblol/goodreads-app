package com.training.dr.androidtraining.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;


public class RatingsProvider extends ContentProvider {
    final String TAG = RatingsProvider.class.getSimpleName();


    public static final Uri RATING_CONTENT_URI = Uri.parse("content://"
            + DataBaseUtils.AUTHORITY_RATINGS + "/" + DBHelper.RATINGS_TABLE);

    static final String RATING_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + DataBaseUtils.AUTHORITY_RATINGS + "." + DBHelper.RATINGS_TABLE;

    static final String RATING_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + DataBaseUtils.AUTHORITY_RATINGS + "." + DBHelper.RATINGS_TABLE;


    static final int URI_RATINGS = 1;
    static final int URI_RATINGS_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_RATINGS, DBHelper.RATINGS_TABLE, URI_RATINGS);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_RATINGS, DBHelper.RATINGS_TABLE + "/#", URI_RATINGS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query, " + uri.toString());

        db = dbHelper.getWritableDatabase();
        Context context = getContext();
        if (context == null) {
            return null;
        }
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_RATINGS:
                Log.d(TAG, "URI_RATINGS");
                cursor = db.query(DBHelper.RATINGS_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        RATING_CONTENT_URI);
                break;
            case URI_RATINGS_ID:
                String bookId = uri.getLastPathSegment();
                Log.d(TAG, "URI_RATINGS_ID, " + bookId);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.RATING_ID + " = " + bookId;
                } else {
                    selection = selection + " AND " + DataBaseUtils.BOOK_ID + " = " + bookId;
                }
                cursor = db.query(DBHelper.RATINGS_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        RATING_CONTENT_URI);
                break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert, " + uri.toString());
        db = dbHelper.getWritableDatabase();
        Uri resultUri;
        long userRowId;
        switch (uriMatcher.match(uri)) {
            case URI_RATINGS:
                Log.d(TAG, "URI_RATINGS");
                userRowId = db.insertWithOnConflict(DBHelper.RATINGS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                resultUri = ContentUris.withAppendedId(RATING_CONTENT_URI, userRowId);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(resultUri, null);
        }

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RATINGS:
                Log.d(TAG, "URI_RATINGS");
                break;
            case URI_RATINGS_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_RATINGS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.RATING_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.RATING_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(DBHelper.RATINGS_TABLE, selection, selectionArgs);
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        String id;
        int cnt = 0;
        db = dbHelper.getWritableDatabase();
        Context context = getContext();
        if (context == null) {
            return 0;
        }
        switch (uriMatcher.match(uri)) {
            case URI_RATINGS:
                Log.d(TAG, "URI_RATINGS");
                break;
            case URI_RATINGS_ID:
                id = uri.getLastPathSegment();
                Log.d(TAG, "URI_RATINGS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.RATING_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.RATING_ID + " = " + id;
                }
                cnt = db.update(DBHelper.RATINGS_TABLE, values, selection, selectionArgs);
                context.getContentResolver().notifyChange(uri, null);
                break;
        }
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_RATINGS:
                return RATING_CONTENT_TYPE;
            case URI_RATINGS_ID:
                return RATING_CONTENT_ITEM_TYPE;
        }
        return null;
    }

}

