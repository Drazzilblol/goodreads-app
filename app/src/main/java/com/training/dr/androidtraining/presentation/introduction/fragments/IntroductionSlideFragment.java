package com.training.dr.androidtraining.presentation.introduction.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.dr.androidtraining.R;

public class IntroductionSlideFragment extends Fragment {
    private static final String SLIDE_POSITION = "SLIDE_POSITION";

    private int slidePosition;
    private View v;

    public IntroductionSlideFragment() {
    }

    public static IntroductionSlideFragment newInstance(int slideNumber) {
        IntroductionSlideFragment fragment = new IntroductionSlideFragment();
        Bundle args = new Bundle();
        args.putInt(SLIDE_POSITION, slideNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.slide_introduction, container, false);
        initArguments();
        initViews();
        return v;
    }

    private void initArguments() {
        if (getArguments() != null) {
            slidePosition = getArguments().getInt(SLIDE_POSITION);
        }
    }

    private void initViews() {
        TypedArray images = getResources().obtainTypedArray(R.array.intro_images);
        ImageView imageView = (ImageView) v.findViewById(R.id.intro_slide_image);
        imageView.setImageDrawable(images.getDrawable(slidePosition));
        TextView textView = (TextView) v.findViewById(R.id.intro_slide_text);
        String[] descriptions = getResources().getStringArray(R.array.intro_desc);
        textView.setText(descriptions[slidePosition]);
        images.recycle();
    }

}
