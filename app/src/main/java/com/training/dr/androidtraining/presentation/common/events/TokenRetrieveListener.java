package com.training.dr.androidtraining.presentation.common.events;

public interface TokenRetrieveListener {
    void onRetrieveSecret();

    void onRetrieveToken(String url);

}
