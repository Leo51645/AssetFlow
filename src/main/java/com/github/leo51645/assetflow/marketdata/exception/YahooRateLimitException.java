package com.github.leo51645.assetflow.marketdata.exception;

public class YahooRateLimitException extends YahooApiException {
    public YahooRateLimitException() {
        super("Yahoo finance rate limit reached, please try again later", cause);
    }
}
