package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

public class YahooConnectionException extends YahooApiException {
    public YahooConnectionException(Throwable cause) {
        super("Failed to connect to Yahoo finance", cause);
    }
}
