package com.training.dr.androidtraining.data.api;


import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.presentation.common.events.TokenRetrieveListener;
import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.Utils;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class GoodreadApi {

    private static final String AUTHORIZATION_WEBSITE_URL = "http://www.goodreads.com/oauth/authorize?mobile=1";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "http://www.goodreads.com/oauth/access_token";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "http://www.goodreads.com/oauth/request_token";
    public static final String CALLBACK_URL = "http://disgustingmen.com/";

    private DefaultOAuthConsumer oAuthConsumer;
    private DefaultOAuthProvider oAuthProvider;
    private static volatile boolean LOGGED_IN = false;
    private SharedPreferences preferences = SPreferences.getInstance().getSharedPreferences();
    private static volatile GoodreadApi instance;
    private static Handler handler;

    public static GoodreadApi getInstance() {
        GoodreadApi result = instance;
        if (result == null) {
            synchronized (GoodreadApi.class) {
                result = instance;
                if (result == null) {
                    instance = result = new GoodreadApi();
                }
            }
        }
        return result;
    }

    private GoodreadApi() {
        oAuthConsumer = new DefaultOAuthConsumer(BuildConfig.GOODREAD_API_KEY, BuildConfig.GOODREAD_API_SECRET);
        oAuthProvider = new DefaultOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZATION_WEBSITE_URL);
        getTokensFromPreferences();
        LOGGED_IN = !TextUtils.isEmpty(oAuthConsumer.getToken()) && !TextUtils.isEmpty(oAuthConsumer.getTokenSecret());
    }

    private void getTokensFromPreferences() {
        String token = preferences.getString(Utils.OAUTH_TOKEN, null);
        String tokenSecret = preferences.getString(Utils.OAUTH_SECRET, null);

        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
            oAuthConsumer.setTokenWithSecret(token, tokenSecret);
        }
    }

    public void login(TokenRetrieveListener listener) {
        new RetrieveRequestTokenTask(listener, oAuthConsumer, oAuthProvider).execute(CALLBACK_URL);
    }

    public void retrieveAccessToken(final String oAuthToken, final TokenRetrieveListener listener) {
        handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                new RetrieveAccessTokenTask(oAuthConsumer, oAuthProvider, oAuthToken, preferences, listener).execute();
            }
        };
        handler.postDelayed(r, 300);

    }

    public DefaultOAuthConsumer getoAuthConsumer() {
        return oAuthConsumer;
    }

    public void clearAuthInformation() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Utils.OAUTH_TOKEN);
        editor.remove(Utils.OAUTH_SECRET);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return LOGGED_IN;
    }

    public void setLoggedIn(boolean loggedIn) {
        LOGGED_IN = loggedIn;
    }
}
