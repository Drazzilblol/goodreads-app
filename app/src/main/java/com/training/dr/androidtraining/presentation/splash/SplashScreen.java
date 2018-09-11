package com.training.dr.androidtraining.presentation.splash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.Utils;

public class SplashScreen extends AppCompatActivity {
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Runnable r = () -> {
            GoodreadApi goodreadApi = GoodreadApi.getInstance();
            SharedPreferences preferences = SPreferences.getInstance().getSharedPreferences();
            if (preferences.getBoolean(Utils.FIRST_RUN, true)) {
                Navigator.goToIntroductionScreen(SplashScreen.this);
            } else if (goodreadApi.isLoggedIn()) {
                Navigator.goToMainScreen(SplashScreen.this);
            } else {
                Navigator.goToLoginScreen(SplashScreen.this);
            }
        };
        handler.postDelayed(r, 2500);
    }
}
