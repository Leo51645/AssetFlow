package com.github.leo51645.assetflow.marketdata.exception;

public class YahooIsinNotFoundException extends YahooApiException {
    public YahooIsinNotFoundException(String requestParam) {
        super("Isin not found for request: " + requestParam, cause);
    }
}
