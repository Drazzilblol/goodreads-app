package com.training.dr.androidtraining.presentation.introduction.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.training.dr.androidtraining.presentation.introduction.fragments.IntroductionSlideFragment;

public class IntroductionFragmentPageAdapter extends FragmentStatePagerAdapter {

    public IntroductionFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroductionSlideFragment.newInstance(position);

    }

    @Override
    public int getCount() {
        return 3;
    }

}
