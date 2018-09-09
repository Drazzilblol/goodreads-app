package com.training.dr.androidtraining.presentation.common.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.presentation.common.views.CustomRatingBar;
import com.training.dr.androidtraining.presentation.common.adapters.BooksCursorAdapter;
import com.training.dr.androidtraining.presentation.common.decorator.OffsetItemDecorator;
import com.training.dr.androidtraining.presentation.common.events.OnBookItemClickListener;
import com.training.dr.androidtraining.presentation.common.events.OnDataChangedListener;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.SPreferences;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListFragment extends Fragment implements OnDataChangedListener,
        OnBookItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    protected LinearLayoutManager linearLayoutManager;
    protected BooksCursorAdapter cursorAdapter;

    protected RecyclerView recyclerView;
    protected List<Item> items = new ArrayList<>();
    protected Cursor cursor;
    private static final String DEFAULT_PAGE_SIZE = "20";
    private String tag;
    private SharedPreferences sharedPreferences = SPreferences.getInstance().getSharedPreferences();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_book_list, container, false);
        tag = getResources().getString(R.string.book_list_fragment_title);
        getLoaderManager().initLoader(1, null, this).forceLoad();
        return recyclerView;
    }

    protected void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new OffsetItemDecorator(getContext(), R.dimen.views_padding_normal));
        cursorAdapter = new BooksCursorAdapter(items, this, cursor, this);
        recyclerView.setAdapter(cursorAdapter);
    }

    @Override
    public void onClick(View v, int position) {
        Item item = items.get(position);
        CustomRatingBar customRatingBar = (CustomRatingBar) v.findViewById(R.id.cv_rating_bar);
        if (item != null && item instanceof Book) {
            if (!customRatingBar.isIndicator()) {
                Navigator.goToBookDetailsScreenWithTransition(getActivity(), ((Book) item).getId());
            } else {
                Toast.makeText(getActivity(), getString(R.string.rating_updating), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    protected int getListSizeFromPreferences(String listSizeKey) {
        return Integer.parseInt(sharedPreferences.getString(listSizeKey, DEFAULT_PAGE_SIZE));
    }

    @Override
    public void onResume() {
        super.onResume();
        OnFragmentLoadedListener listener = (OnFragmentLoadedListener) getActivity();
        listener.onFragmentLoaded(tag);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
