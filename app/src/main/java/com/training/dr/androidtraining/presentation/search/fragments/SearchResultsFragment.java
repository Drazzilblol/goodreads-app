package com.training.dr.androidtraining.presentation.search.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.domain.loaders.SearchCursorLoader;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;
import com.training.dr.androidtraining.presentation.common.views.CustomRatingBar;
import com.training.dr.androidtraining.ulils.Navigator;

public class SearchResultsFragment extends AbstractListFragment {

    private static final String TAG = SearchResultsFragment.class.getSimpleName();
    private static final String QUERY = "query";
    private String query;
    private String tag;

    public static AbstractListFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initArguments();
        tag = getResources().getString(R.string.search_results_fragment_title);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    protected void initArguments() {
        if (getArguments() != null) {
            query = getArguments().getString(QUERY);
        }
    }

    @Override
    public void onClick(View v, int position) {
        Item item = items.get(position);
        CustomRatingBar customRatingBar = (CustomRatingBar) v.findViewById(R.id.cv_rating_bar);
        if (item != null && item instanceof Book) {
            if (!customRatingBar.isIndicator()) {
                Navigator.goToBookDetailsScreen(getActivity(), ((Book) item).getId());
            } else {
                Toast.makeText(getActivity(), getString(R.string.rating_updating), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new SearchCursorLoader(getActivity(), new String[]{query});
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader instanceof SearchCursorLoader) {
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

    public void search(String query) {
        this.query = query;
        getLoaderManager().restartLoader(1, null, this);
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
