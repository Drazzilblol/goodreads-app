package com.training.dr.androidtraining.presentation.main.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.domain.loaders.SortedBooksLoader;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;

public class RatedBooksListFragment extends AbstractListFragment {

    private static final String TAG = RatedBooksListFragment.class.getSimpleName();
    private String key;
    private int size;

    public static AbstractListFragment newInstance() {
        return new RatedBooksListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        key = getResources().getString(R.string.top_preference_key);
        size = getListSizeFromPreferences(key);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            getListSizeFromPreferences(key);
            return new SortedBooksLoader(getActivity(), size, SortedBooksLoader.BY_RATING);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader instanceof SortedBooksLoader) {
            cursor = data;
            if (recyclerView.getAdapter() == null) {
                initRecyclerView();
            } else {
                cursorAdapter.swapCursor(cursor);
            }
        } else {
            Log.d(TAG, "Wrong Loader");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(key, this.key)) {
            size = getListSizeFromPreferences(key);
            getLoaderManager().restartLoader(1, null, this).forceLoad();
        }
    }

    @Override
    public void onDataChanged() {
        getLoaderManager().restartLoader(1, null, this).forceLoad();
    }
}
