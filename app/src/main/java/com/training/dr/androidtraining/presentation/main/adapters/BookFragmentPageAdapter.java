package com.training.dr.androidtraining.presentation.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.main.fragments.BookListFragment;
import com.training.dr.androidtraining.presentation.main.fragments.NewBooksListFragment;
import com.training.dr.androidtraining.presentation.main.fragments.RatedBooksListFragment;

public class BookFragmentPageAdapter extends FragmentStatePagerAdapter {
    private int userId;
    private String[] pages;

    public BookFragmentPageAdapter(FragmentManager fm, int userId, Context context) {
        super(fm);
        this.userId = userId;
        pages = context.getResources().getStringArray(R.array.pages);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BookListFragment.newInstance(userId);
            case 1:
                return RatedBooksListFragment.newInstance();
            case 2:
                return NewBooksListFragment.newInstance();
            default:
                return BookListFragment.newInstance(userId);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages[position];
    }
}
