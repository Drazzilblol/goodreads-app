package com.training.dr.androidtraining.data.database;

import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.util.HashMap;


public class BooksProvider extends SearchRecentSuggestionsProvider {
    final String TAG = BooksProvider.class.getSimpleName();

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"
            + DataBaseUtils.AUTHORITY_BOOKS + "/" + DBHelper.BOOKS_TABLE);

    static final String BOOK_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + DataBaseUtils.AUTHORITY_BOOKS + "." + DBHelper.BOOKS_TABLE;

    static final String BOOK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + DataBaseUtils.AUTHORITY_BOOKS + "." + DBHelper.BOOKS_TABLE;

    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    private static final int URI_BOOKS = 1;
    private static final int URI_BOOKS_ID = 2;
    private static final int URI_SUGGESTIONS = 3;
    private static final int URI_MATCH_SUGGEST = 4;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_BOOKS, DBHelper.BOOKS_TABLE, URI_BOOKS);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_BOOKS, DBHelper.BOOKS_TABLE + "/#", URI_BOOKS_ID);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_BOOKS, SearchManager.SUGGEST_URI_PATH_QUERY, URI_SUGGESTIONS);
        uriMatcher.addURI(DataBaseUtils.AUTHORITY_BOOKS, "suggestions", URI_MATCH_SUGGEST);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;
    HashMap<String, String> projectionMap;

    public BooksProvider() {
        setupSuggestions(DataBaseUtils.AUTHORITY_BOOKS, MODE);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        dbHelper = new DBHelper(getContext());

        projectionMap = new HashMap<>();
        projectionMap.put("_ID", DataBaseUtils.BOOK_ID + " as " + "_id");
        projectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, DataBaseUtils.BOOK_TITLE + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        projectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_2, DataBaseUtils.BOOK_DESCRIPTION + " as " + SearchManager.SUGGEST_COLUMN_TEXT_2);
        projectionMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, DataBaseUtils.BOOK_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
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
            case URI_BOOKS:
                Log.d(TAG, "URI_BOOKS");
                String table = DBHelper.BOOKS_TABLE + " as BK LEFT OUTER JOIN " + DBHelper.FAVORED_TABLE + " as FV on BK.favorites = FV.id";
                cursor = db.query(table, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        BOOK_CONTENT_URI);
                break;
            case URI_BOOKS_ID:
                String bookId = uri.getLastPathSegment();
                Log.d(TAG, "URI_BOOKS_ID, " + bookId);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.BOOK_ID + " = " + bookId;
                } else {
                    selection = selection + " AND " + DataBaseUtils.BOOK_ID + " = " + bookId;
                }
                cursor = db.query(DBHelper.BOOKS_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(context.getContentResolver(),
                        BOOK_CONTENT_URI);
                break;
            case URI_SUGGESTIONS:

                Cursor customSugg = getBooksSuggestions(selectionArgs);
                Cursor recentSugg = super.query(uri, projection, selection, selectionArgs, sortOrder);
                cursor = joinCursors(customSugg, recentSugg);
                break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return cursor;
    }

    private Cursor getBooksSuggestions(String[] selectionArgs) {
        String selection = DataBaseUtils.BOOK_TITLE + " like ? ";

        if (selectionArgs != null) {
            selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(projectionMap);

        queryBuilder.setTables(DBHelper.BOOKS_TABLE);

        Cursor c = queryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{
                        "_ID",
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                },
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return c;
    }

    private Cursor joinCursors(Cursor c1, Cursor c2) {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                "_ID",
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
        });
        addCustomSuggestions(cursor, c1);
        addRecentSuggestions(cursor, c2);
        return cursor;
    }

    private void addCustomSuggestions(MatrixCursor cursor, Cursor c1) {
        if (c1.getCount() != 0) {
            while (!c1.isLast()) {
                c1.moveToNext();
                String iconUri = "android.resource://" + getContext().getPackageName() + "/drawable/ic_book_black_24dp";
                cursor.addRow(new String[]{c1.getString(0), c1.getString(1), c1.getString(2), iconUri, c1.getString(3)});
            }
        }
    }

    private void addRecentSuggestions(MatrixCursor cursor, Cursor c2) {
        if (c2.getCount() != 0) {
            while (!c2.isLast()) {
                c2.moveToNext();
                cursor.addRow(new String[]{c2.getString(5), c2.getString(2), c2.getString(3), c2.getString(1), c2.getString(4)});
            }
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert, " + uri.toString());
        db = dbHelper.getWritableDatabase();
        Uri resultUri = null;
        long userRowId;
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(TAG, "URI_BOOKS");
                userRowId = db.insertWithOnConflict(DBHelper.BOOKS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                resultUri = ContentUris.withAppendedId(BOOK_CONTENT_URI, userRowId);
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(resultUri, null);
                }
                return resultUri;
            case URI_MATCH_SUGGEST:
                super.insert(uri, values);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(TAG, "URI_BOOKS");
                break;
            case URI_BOOKS_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_BOOKS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.BOOK_ID + " = " + id;
                }
                db = dbHelper.getWritableDatabase();
                int cnt = db.delete(DBHelper.BOOKS_TABLE, selection, selectionArgs);
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }
                return cnt;
            case URI_MATCH_SUGGEST:
                super.delete(uri, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        String id;
        int cnt;
        db = dbHelper.getWritableDatabase();
        Context context = getContext();
        if (context == null) {
            return 0;
        }
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(TAG, "URI_BOOKS");
                break;
            case URI_BOOKS_ID:
                id = uri.getLastPathSegment();
                Log.d(TAG, "URI_BOOKS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseUtils.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseUtils.BOOK_ID + " = " + id;
                }
                break;
        }
        cnt = db.update(DBHelper.BOOKS_TABLE, values, selection, selectionArgs);
        context.getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                return BOOK_CONTENT_TYPE;
            case URI_BOOKS_ID:
                return BOOK_CONTENT_ITEM_TYPE;
        }
        return null;
    }

}

