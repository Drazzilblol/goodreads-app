package com.training.dr.androidtraining.presentation.main.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.services.ApiGetService;
import com.training.dr.androidtraining.domain.loaders.BookListCursorLoader;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;
import com.training.dr.androidtraining.presentation.main.scroll.EndlessRecyclerViewScrollListener;
import com.training.dr.androidtraining.ulils.Utils;

public class BookListFragment extends AbstractListFragment {
    private static final String USER_ID = "USER_ID";
    private static final String TAG = BookListFragment.class.getSimpleName();

    private int userId;
    private AlertDialog dialog;
    private BroadcastReceiver br;
    private IntentFilter intentFilter;

    public static AbstractListFragment newInstance(int userId) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        createDialog();
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(Utils.PARAM_STATUS, 0);
                if (status == Utils.STATUS_ERROR) {
                    dialog.show();
                }
            }
        };
        intentFilter = new IntentFilter(Utils.BROADCAST_ACTION);
        initArguments();
        getActivity().registerReceiver(br, intentFilter);

        return v;
    }

    protected void initArguments() {
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.loading_error);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.loading_error_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                startDownloadService(1);
            }
        });
        dialog = builder.create();
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (loader instanceof BookListCursorLoader) {
            cursor = data;
            if (recyclerView.getAdapter() == null) {
                initRecyclerView();
                if (cursor.getCount() == 0) {
                    startDownloadService(1);
                }
            } else {
                if (cursorAdapter.swapCursor(cursor)) {
                    cursorAdapter.setLoading(false);
                }
            }
        } else {
            Log.d(TAG, "Wrong Loader");
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                startDownloadService(page);
            }
        });
    }

    private void startDownloadService(int page) {
        Context context = getActivity();
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, ApiGetService.class);
        intent.putExtra("url", BuildConfig.BASE_URL + ApiMethods.USER_BOOKS_BY_ID + userId + "?key=" + BuildConfig.GOODREAD_API_KEY + "&page=" + page + "&v=2");
        context.startService(intent);
        cursorAdapter.setLoading(true);
    }

    @Override
    public void onDataChanged() {
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new BookListCursorLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(br);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(br, intentFilter);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}
