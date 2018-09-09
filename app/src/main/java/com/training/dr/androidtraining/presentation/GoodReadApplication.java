package com.training.dr.androidtraining.presentation;

import android.app.Application;

import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.image.CacheImageConfigManager;


public class GoodReadApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SPreferences.initSPreferences(this);

        CacheImageConfigManager.buildConfig(this)
                .diskCacheFolderName(CacheImageConfigManager.DEFAULT_CACHE_FOLDER_NAME)
                .enableDiskCache(true)
                .build();
    }
}
