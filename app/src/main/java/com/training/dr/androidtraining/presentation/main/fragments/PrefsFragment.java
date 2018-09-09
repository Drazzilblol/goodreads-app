package com.training.dr.androidtraining.presentation.main.fragments;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;

public class PrefsFragment extends PreferenceFragmentCompat {

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }

    public PrefsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onResume() {
        super.onResume();
        OnFragmentLoadedListener listener = (OnFragmentLoadedListener) getActivity();
        String title = getResources().getString(R.string.preferences_fragment_title);
        listener.onFragmentLoaded(title);
    }

}
