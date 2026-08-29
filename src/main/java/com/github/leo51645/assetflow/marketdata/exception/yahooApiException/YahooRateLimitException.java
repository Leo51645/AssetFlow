package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooRateLimitException extends YahooApiException {
    public YahooRateLimitException() {
        super("Yahoo finance rate limit reached, please try again later", HttpStatus.TOO_MANY_REQUESTS);
    }
}
