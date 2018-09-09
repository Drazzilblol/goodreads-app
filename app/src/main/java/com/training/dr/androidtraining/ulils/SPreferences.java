package com.training.dr.androidtraining.ulils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.training.dr.androidtraining.data.api.GoodreadApi;

public class SPreferences {
    private static volatile SPreferences instance;
    private static volatile SharedPreferences sharedPreferences;

    public static void initSPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SPreferences getInstance() {
        SPreferences result = instance;
        if (result == null) {
            synchronized (GoodreadApi.class) {
                result = instance;
                if (result == null) {
                    instance = result = new SPreferences();
                }
            }
        }
        return result;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
