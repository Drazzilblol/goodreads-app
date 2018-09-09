package com.training.dr.androidtraining.domain.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;


public class FavoredBooksLoader extends CursorLoader {

    private WeakReference<Context> contextRef;
    private static final String SORT = "timestamp desc";
    private static final String SELECTION = "favorites > 0";

    public FavoredBooksLoader(Context context) {
        super(context);
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public Cursor loadInBackground() {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(DataBaseUtils.BOOK_URI, null, SELECTION,
                null, SORT);
    }
}
