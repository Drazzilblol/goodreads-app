package com.training.dr.androidtraining.presentation.favored.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.domain.loaders.FavoredBooksLoader;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;

public class FavoredBooksListFragment extends AbstractListFragment {

    private static final String TAG = FavoredBooksListFragment.class.getSimpleName();
    private String tag;

    public static AbstractListFragment newInstance() {
        return new FavoredBooksListFragment();
    }

    public void filter(String query) {
        cursorAdapter.filter(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tag = getResources().getString(R.string.favored_book_fragment_title);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new FavoredBooksLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader instanceof FavoredBooksLoader) {
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
    public void onDataChanged() {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onResume() {
        super.onResume();
        OnFragmentLoadedListener listener = (OnFragmentLoadedListener) getActivity();
        listener.onFragmentLoaded(tag);
    }

}
