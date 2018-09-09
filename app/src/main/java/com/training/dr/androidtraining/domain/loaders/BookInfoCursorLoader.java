package com.training.dr.androidtraining.domain.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;


public class BookInfoCursorLoader extends CursorLoader {

    private WeakReference<Context> contextRef;
    private int id;

    public BookInfoCursorLoader(Context context, int id) {
        super(context);
        this.contextRef = new WeakReference<>(context);
        this.id = id;
    }

    @Override
    public Cursor loadInBackground() {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(Uri.withAppendedPath(DataBaseUtils.BOOK_URI, id + ""), null, null,
                null, null);
    }
}
