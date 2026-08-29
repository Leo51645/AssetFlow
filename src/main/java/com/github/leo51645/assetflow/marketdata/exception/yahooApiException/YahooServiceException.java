package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooServiceException extends YahooApiException {
    public YahooServiceException(int statusCode) {
        super("Yahoo finance service error with status code: " + statusCode, HttpStatus.valueOf(statusCode));
    }
}
