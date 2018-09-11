package com.training.dr.androidtraining.presentation.favored;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.common.events.OnDataChangedListener;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;
import com.training.dr.androidtraining.presentation.favored.fragments.FavoredBooksListFragment;
import com.training.dr.androidtraining.ulils.FragmentsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoredBooks extends AppCompatActivity implements OnDataChangedListener,
        SearchView.OnQueryTextListener,
        OnFragmentLoadedListener {

    private String tag;

    @BindView(R.id.activity_favored_toolbar)
    Toolbar toolbar;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_favored_books);
        ButterKnife.bind(this);
        setupWindowAnimations();
        fragmentManager = getSupportFragmentManager();
        tag = getResources().getString(R.string.favored_book_fragment_title);
        initToolbar();
        initFragment();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.END);
            slideTransition.setDuration(500);
            getWindow().setEnterTransition(slideTransition);
            getWindow().setExitTransition(slideTransition);
        }
    }

    private void initToolbar() {

        toolbar.setTitle(tag);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initFragment() {
        AbstractListFragment fragment = FavoredBooksListFragment.newInstance();
        FragmentsUtils.addFragment(
                fragmentManager,
                R.id.frag_cont,
                fragment,
                false,
                false,
                tag);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            toolbar.setTitle(FragmentsUtils.getTopEntryName(fragmentManager));
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_favored_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null && fragment instanceof FavoredBooksListFragment) {
            if (TextUtils.isEmpty(newText)) {
                ((FavoredBooksListFragment) fragment).filter("");
            } else {
                ((FavoredBooksListFragment) fragment).filter(newText);
            }
        }

        return true;
    }

    @Override
    public void onFragmentLoaded(String toolbarTitle) {
        toolbar.setTitle(toolbarTitle);
    }

    @Override
    public void onDataChanged() {
    }

}
