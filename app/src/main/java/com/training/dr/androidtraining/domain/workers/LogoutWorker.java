package com.training.dr.androidtraining.domain.workers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.SearchRecentSuggestions;
import android.webkit.CookieManager;

import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.data.database.BooksProvider;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

import java.lang.ref.WeakReference;

public class LogoutWorker extends AsyncTask<Void, Void, Void> {

    private ContentResolver resolver;
    private WeakReference<Context> contextWeakReference;

    public LogoutWorker(Context context, ContentResolver contentResolver) {
        this.resolver = contentResolver;
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        GoodreadApi api = GoodreadApi.getInstance();
        api.clearAuthInformation();
        CookieManager.getInstance().removeAllCookie();
        resolver.delete(DataBaseUtils.FAVORED_URI, null, null);
        resolver.delete(DataBaseUtils.RATINGS_URI, null, null);
        ContentValues cv = new ContentValues();
        cv.put(DataBaseUtils.BOOK_FAVORITES, "0");
        resolver.update(DataBaseUtils.BOOK_URI, cv, null, null);
        Context context = contextWeakReference.get();
        if (context != null) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context,
                    DataBaseUtils.AUTHORITY_BOOKS, BooksProvider.MODE);
            suggestions.clearHistory();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Context context = contextWeakReference.get();
        if (context != null) {
            Navigator.goToLoginScreenFromMain(context);
        }
    }
}
