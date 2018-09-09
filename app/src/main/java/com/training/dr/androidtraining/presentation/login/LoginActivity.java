package com.training.dr.androidtraining.presentation.login;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.presentation.common.events.TokenRetrieveListener;
import com.training.dr.androidtraining.ulils.Navigator;

import oauth.signpost.OAuth;

public class LoginActivity extends AppCompatActivity implements TokenRetrieveListener {

    private WebView webView;
    private ProgressDialog progress;
    private int counter = 0;
    private GoodreadApi goodreadApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        goodreadApi = GoodreadApi.getInstance();
        initProgress();
        initWebView();
        goodreadApi.login(this);

    }

    private void initProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.authorization));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.activity_login_web_view);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains(GoodreadApi.CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    String token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
                    if (counter == 0) {
                        goodreadApi.retrieveAccessToken(token, LoginActivity.this);
                        progress.show();
                        counter++;
                    }
                }
            }
        });
    }

    @Override
    public void onRetrieveSecret() {
        Navigator.goToMainScreenFromLogin(this);
        progress.dismiss();
    }

    @Override
    public void onRetrieveToken(String url) {
        webView.loadUrl(url);
    }
}
