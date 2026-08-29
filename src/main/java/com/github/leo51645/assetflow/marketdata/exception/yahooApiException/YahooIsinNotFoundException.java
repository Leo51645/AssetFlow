package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooIsinNotFoundException extends YahooApiException {
    public YahooIsinNotFoundException(String requestParam) {
        super("Isin not found for request: " + requestParam, HttpStatus.NOT_FOUND);
    }
}
