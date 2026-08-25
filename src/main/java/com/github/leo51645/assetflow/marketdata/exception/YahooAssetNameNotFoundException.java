package com.github.leo51645.assetflow.marketdata.exception;

public class YahooAssetNameNotFoundException extends YahooApiException {
    public YahooAssetNameNotFoundException(String requestParam) {
        super("Asset name not found for request: " + requestParam, cause);
    }
}
