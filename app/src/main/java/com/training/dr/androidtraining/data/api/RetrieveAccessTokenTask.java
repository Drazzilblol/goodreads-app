package com.training.dr.androidtraining.data.api;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.training.dr.androidtraining.presentation.common.events.TokenRetrieveListener;
import com.training.dr.androidtraining.ulils.Utils;

import java.lang.ref.WeakReference;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class RetrieveAccessTokenTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = RetrieveAccessTokenTask.class.getSimpleName();
    private DefaultOAuthProvider oAuthProvider;
    private DefaultOAuthConsumer oAuthConsumer;
    private String oAuthToken;
    private SharedPreferences sharedPreferences;
    private WeakReference<TokenRetrieveListener> listenerWeakReference;


    public RetrieveAccessTokenTask(DefaultOAuthConsumer oAuthConsumer,
                                   DefaultOAuthProvider oAuthProvider,
                                   String oAuthToken,
                                   SharedPreferences sp,
                                   TokenRetrieveListener listener) {
        this.oAuthConsumer = oAuthConsumer;
        this.oAuthProvider = oAuthProvider;
        this.oAuthToken = oAuthToken;
        this.sharedPreferences = sp;
        this.listenerWeakReference = new WeakReference<>(listener);
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            oAuthProvider.retrieveAccessToken(oAuthConsumer, oAuthToken);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(Utils.OAUTH_TOKEN, oAuthConsumer.getToken());
            edit.putString(Utils.OAUTH_SECRET, oAuthConsumer.getTokenSecret());
            edit.apply();
            GoodreadApi.getInstance().setLoggedIn(true);

        } catch (OAuthMessageSignerException
                | OAuthNotAuthorizedException
                | OAuthCommunicationException
                | OAuthExpectationFailedException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TokenRetrieveListener listener = listenerWeakReference.get();
        if (listener == null) {
            return;
        }
        listener.onRetrieveSecret();
    }
}