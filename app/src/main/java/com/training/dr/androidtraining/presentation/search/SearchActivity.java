package com.training.dr.androidtraining.presentation.search;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.database.BooksProvider;
import com.training.dr.androidtraining.presentation.common.events.OnDataChangedListener;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.presentation.common.fragments.AbstractListFragment;
import com.training.dr.androidtraining.presentation.search.fragments.SearchResultsFragment;
import com.training.dr.androidtraining.ulils.FragmentsUtils;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;

public class SearchActivity extends AppCompatActivity implements OnDataChangedListener,
        OnFragmentLoadedListener {

    private String tag;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_search);
        setupWindowAnimations();
        fragmentManager = getSupportFragmentManager();
        tag = getResources().getString(R.string.favored_book_fragment_title);
        initSearch();
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

    private void initSearch() {
        SearchView searchView = findViewById(R.id.search_view);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, this.getClass())));
        searchView.setQueryRefinementEnabled(true);
        searchView.setIconifiedByDefault(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchView.setElevation(8.0f);
        }

        SearchView.SearchAutoComplete autoCompleteTextView =
                searchView.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundDrawable(getResources()
                    .getDrawable(R.color.colorBackgroundLight));

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                handleSuggestionClick(intent);
            } else if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                doSearch(query);
            }
        }
    }

    protected void handleSuggestionClick(Intent intent) {
        if (intent == null || intent.getData() == null) {
            return;
        }
        String suggestion = intent.getData().getLastPathSegment();
        if (TextUtils.isDigitsOnly(suggestion)) {
            Navigator.goToBookDetailsScreen(this, Integer.parseInt(suggestion));
        } else {
            doSearch(suggestion);
        }
    }

    private void doSearch(String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                DataBaseUtils.AUTHORITY_BOOKS, BooksProvider.MODE);
        suggestions.saveRecentQuery(query, null);
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null && fragment instanceof SearchResultsFragment) {
            ((SearchResultsFragment) fragment).search(query);
        } else {
            AbstractListFragment searchResultsFragment = SearchResultsFragment.newInstance(query);
            FragmentsUtils.addFragment(
                    fragmentManager,
                    R.id.frag_cont,
                    searchResultsFragment,
                    true,
                    true,
                    tag);
        }
    }

    @Override
    public void onFragmentLoaded(String toolbarTitle) {

    }

    @Override
    public void onDataChanged() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}