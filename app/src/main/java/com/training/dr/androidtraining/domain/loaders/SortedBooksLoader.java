package com.training.dr.androidtraining.domain.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;


public class SortedBooksLoader extends CursorLoader {

    public static final String BY_RATING = "BY_RATING";
    public static final String BY_CREADION_DATE = "BY_CREATION_DATE";

    private WeakReference<Context> contextRef;
    private String sortQuery;

    public SortedBooksLoader(Context context, int limit, String sortType) {
        super(context);
        this.contextRef = new WeakReference<>(context);
        if (TextUtils.equals(sortType, BY_RATING)) {
            this.sortQuery = "RATING DESC LIMIT 0," + limit;
        } else if (TextUtils.equals(sortType, BY_CREADION_DATE)) {
            this.sortQuery = "CREATED ASC LIMIT 0," + limit;
        }
    }

    @Override
    public Cursor loadInBackground() {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(DataBaseUtils.BOOK_URI, null, null,
                null, sortQuery);
    }
}
