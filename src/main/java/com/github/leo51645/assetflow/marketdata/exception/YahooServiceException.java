package com.github.leo51645.assetflow.marketdata.exception;

public class YahooServiceException extends RuntimeException {
    public YahooServiceException(int statusCode) {
        super("Yahoo finance service error with status code: " + statusCode);
    }
}
