package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooAssetNameNotFoundException extends YahooApiException {
    public YahooAssetNameNotFoundException(String requestParam) {
        super("Asset name not found for request: " + requestParam, HttpStatus.NOT_FOUND);
    }
}
