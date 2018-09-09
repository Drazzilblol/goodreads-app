package com.training.dr.androidtraining.presentation.introduction;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.common.views.Dots;
import com.training.dr.androidtraining.presentation.introduction.adapters.IntroductionFragmentPageAdapter;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.Utils;

public class IntroductionActivity extends AppCompatActivity implements View.OnClickListener {

    private static int PAGE_COUNT = 3;

    private ViewPager viewPager;
    private IntroductionFragmentPageAdapter introductionFragmentPageAdapter;
    private Dots dots;
    private Button btnBack, btnNext, btnSkip, btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_introduction);

        viewPager = (ViewPager) findViewById(R.id.activity_introduction_view_pager);
        dots = (Dots) findViewById(R.id.dots);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnStart = (Button) findViewById(R.id.btn_start);

        dots.setCheckedPosition(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.changeStatusBarColor(getWindow());
        }

        introductionFragmentPageAdapter = new IntroductionFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(introductionFragmentPageAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int current = viewPager.getCurrentItem();
        if (v.getId() == btnNext.getId()) {
            if (current < PAGE_COUNT) {
                viewPager.setCurrentItem(current + 1);
            }
        }
        if (v.getId() == btnBack.getId()) {
            if (current > 0) {
                viewPager.setCurrentItem(current - 1);
            }
        }
        if (v.getId() == btnSkip.getId()) {
            SharedPreferences preferences = SPreferences.getInstance().getSharedPreferences();
            preferences.edit().putBoolean(Utils.FIRST_RUN, false).apply();
            Navigator.goToLoginScreen(this);
        }
        if (v.getId() == btnStart.getId()) {
            SharedPreferences preferences = SPreferences.getInstance().getSharedPreferences();
            preferences.edit().putBoolean(Utils.FIRST_RUN, false).apply();
            Navigator.goToLoginScreen(this);
        }

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            dots.setCheckedPosition(position);
            if (position == PAGE_COUNT - 1) {
                btnNext.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.GONE);
            }

            if (position == 0) {
                btnBack.setVisibility(View.GONE);
            } else {
                btnBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

}


