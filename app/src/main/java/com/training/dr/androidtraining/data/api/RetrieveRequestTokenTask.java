package com.training.dr.androidtraining.data.api;


import android.os.AsyncTask;
import android.util.Log;

import com.training.dr.androidtraining.presentation.common.events.TokenRetrieveListener;

import java.lang.ref.WeakReference;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class RetrieveRequestTokenTask extends AsyncTask<String, Void, String> {
    private static final String TAG = RetrieveRequestTokenTask.class.getSimpleName();

    private DefaultOAuthProvider oAuthProvider;
    private DefaultOAuthConsumer oAuthConsumer;
    private WeakReference<TokenRetrieveListener> listenerWeakReference;

    public RetrieveRequestTokenTask(TokenRetrieveListener listener,
                                    DefaultOAuthConsumer oAuthConsumer,
                                    DefaultOAuthProvider oAuthProvider) {
        this.oAuthConsumer = oAuthConsumer;
        this.oAuthProvider = oAuthProvider;
        this.listenerWeakReference = new WeakReference<>(listener);
    }

    @Override
    protected String doInBackground(String... params) {
        String authUrl = "";
        try {
            authUrl = oAuthProvider.retrieveRequestToken(
                    oAuthConsumer,
                    params[0]);
        } catch (OAuthMessageSignerException
                | OAuthNotAuthorizedException
                | OAuthCommunicationException
                | OAuthExpectationFailedException e) {
            Log.e(TAG, e.getMessage());
        }
        return authUrl;
    }

    @Override
    protected void onPostExecute(String s) {
        TokenRetrieveListener listener = listenerWeakReference.get();
        if (listener == null) {
            return;
        }
        listener.onRetrieveToken(s);
    }
}