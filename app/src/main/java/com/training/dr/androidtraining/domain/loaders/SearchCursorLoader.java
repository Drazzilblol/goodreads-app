package com.training.dr.androidtraining.domain.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;


public class SearchCursorLoader extends CursorLoader {

    private WeakReference<Context> contextRef;
    private String selection = null;


    public SearchCursorLoader(Context context, String[] selectionArgs) {
        super(context);
        this.contextRef = new WeakReference<>(context);
        if (selectionArgs != null) {
            selection = DataBaseUtils.BOOK_TITLE + " like \'%" + selectionArgs[0] + "%\' ";
        }
    }

    @Override
    public Cursor loadInBackground() {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }
        return context.getContentResolver().query(DataBaseUtils.BOOK_URI, null, selection,
                null, null);
    }
}
