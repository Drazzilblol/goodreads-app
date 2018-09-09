package com.training.dr.androidtraining.domain.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;


public class BookListCursorLoader extends CursorLoader {

    private WeakReference<Context> contextRef;
    private static final String SORT_QUERY = "CREATED DESC";

    public BookListCursorLoader(Context context) {
        super(context);
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public Cursor loadInBackground() {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(DataBaseUtils.BOOK_URI, null, null,
                null, SORT_QUERY);
    }
}
