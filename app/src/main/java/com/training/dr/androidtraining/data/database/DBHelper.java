package com.training.dr.androidtraining.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "goodread";
    private static final int DB_VERSION = 1;

    static final String BOOKS_TABLE = "books";

    private static final String BOOKS_DB_CREATE = "create table " + BOOKS_TABLE + "("
            + DataBaseUtils.BOOK_ID + " integer primary key, "
            + DataBaseUtils.BOOK_GOODREAD_ID + " integer, "
            + DataBaseUtils.BOOK_REVIEW_ID + " integer, "
            + DataBaseUtils.BOOK_TITLE + " text, "
            + DataBaseUtils.BOOK_IMAGE_URL + " text, "
            + DataBaseUtils.BOOK_AUTHOR + " text, "
            + DataBaseUtils.BOOK_YEAR + " text, "
            + DataBaseUtils.BOOK_RATING + " real, "
            + DataBaseUtils.BOOK_MY_RATING + " integer, "
            + DataBaseUtils.BOOK_DESCRIPTION + " text, "
            + DataBaseUtils.BOOK_CREATED + " text, "
            + DataBaseUtils.BOOK_FAVORITES + " integer" + ");";

    static final String RATINGS_TABLE = "ratings";

    private static final String RATINGS_DB_CREATE = "create table " + RATINGS_TABLE + "("
            + DataBaseUtils.RATING_ID + " integer primary key autoincrement, "
            + DataBaseUtils.RATING_BOOK_ID + " integer, "
            + DataBaseUtils.RATING_RATE + " integer, "
            + DataBaseUtils.RATING_RATE_TIMESTAMP + " integer" + ");";

    static final String FAVORED_TABLE = "favorites";

    private static final String FAVORED_DB_CREATE = "create table " + FAVORED_TABLE + "("
            + DataBaseUtils.FAVORED_ID + " integer primary key autoincrement, "
            + DataBaseUtils.FAVORED_BOOK_ID + " integer, "
            + DataBaseUtils.FAVORED_TIMESTAMP + " integer" + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOOKS_DB_CREATE);
        db.execSQL(RATINGS_DB_CREATE);
        db.execSQL(FAVORED_DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}