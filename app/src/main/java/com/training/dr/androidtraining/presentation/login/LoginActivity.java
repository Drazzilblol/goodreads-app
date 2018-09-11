package com.training.dr.androidtraining.presentation.login;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.oauth.OAuth10aService;
import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.GoodreadApi;
import com.training.dr.androidtraining.presentation.common.events.TokenRetrieveListener;
import com.training.dr.androidtraining.ulils.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import oauth.signpost.OAuth;

import static com.training.dr.androidtraining.data.api.GoodreadApi.CALLBACK_URL;

public class LoginActivity extends AppCompatActivity implements TokenRetrieveListener {

    @BindView(R.id.activity_login_web_view)
    WebView webView;

    private ProgressDialog progress;
    private int counter = 0;
    private GoodreadApi goodreadApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
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
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains(CALLBACK_URL)) {
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
