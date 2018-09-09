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


public class FavoredProvider extends ContentProvider {
    final String TAG = FavoredProvider.class.getSimpleName();


    public static final Uri FAVORITED_CONTENT_URI = Uri.parse("content://"
            + DataBaseUtils.AUTHORITY_FAVORITED + "/" + DBHelper.FAVORED_TABLE);

    static final String FAVORITED_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + DataBaseUtils.AUTHORITY_FAVORITED + "." + DBHelper.FAVORED_TABLE;

    static final String FAVORITED_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + DataBaseUtils.AUTHORITY_FAVORITED + "." + DBHelper.FAVORED_TABLE;


    static final int URI_FAVORITED = 1;
    static final int URI_FAVORITED_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_FAVORITED, DBHelper.FAVORED_TABLE, URI_FAVORITED);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_FAVORITED, DBHelper.FAVORED_TABLE + "/#", URI_FAVORITED_ID);
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
            case URI_FAVORITED:
                Log.d(TAG, "URI_BOOKS");
                String table = DBHelper.FAVORED_TABLE + " as FV inner join " + DBHelper.BOOKS_TABLE + " as BK on FV.book_id = BK.id";
                cursor = db.query(table, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        FAVORITED_CONTENT_URI);
                break;
            case URI_FAVORITED_ID:
                String bookId = uri.getLastPathSegment();
                Log.d(TAG, "URI_BOOKS_ID, " + bookId);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.BOOK_ID + " = " + bookId;
                } else {
                    selection = selection + " AND " + DataBaseUtils.FAVORED_ID + " = " + bookId;
                }
                cursor = db.query(DBHelper.FAVORED_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        FAVORITED_CONTENT_URI);
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
            case URI_FAVORITED:
                Log.d(TAG, "URI_FAVORITED");
                userRowId = db.insertWithOnConflict(DBHelper.FAVORED_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                resultUri = ContentUris.withAppendedId(FAVORITED_CONTENT_URI, userRowId);
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
            case URI_FAVORITED:
                Log.d(TAG, "URI_FAVORITED");
                break;
            case URI_FAVORITED_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_FAVORITED_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.FAVORED_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.FAVORED_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(DBHelper.FAVORED_TABLE, selection, selectionArgs);
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
            case URI_FAVORITED:
                Log.d(TAG, "URI_FAVORITED");
                break;
            case URI_FAVORITED_ID:
                id = uri.getLastPathSegment();
                Log.d(TAG, "URI_FAVORITED_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.FAVORED_ID + " = " + id;
                }
                cnt = db.update(DBHelper.FAVORED_TABLE, values, selection, selectionArgs);
                context.getContentResolver().notifyChange(uri, null);
                break;
        }
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_FAVORITED:
                return FAVORITED_CONTENT_TYPE;
            case URI_FAVORITED_ID:
                return FAVORITED_CONTENT_ITEM_TYPE;
        }
        return null;
    }

}

