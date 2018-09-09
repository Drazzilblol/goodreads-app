package com.training.dr.androidtraining.presentation.common.customtabs;

import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.style.URLSpan;
import android.view.View;

public class CustomTabsURLSpan extends URLSpan {

    public CustomTabsURLSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        String url = getURL();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(Color.CYAN);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(widget.getContext(), Uri.parse(url));
    }
}