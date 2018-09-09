package com.training.dr.androidtraining.ulils;

import android.os.Environment;


public class StorageUtils {

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


}