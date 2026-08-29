package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooSymbolNotFoundException extends YahooApiException {
    public YahooSymbolNotFoundException(String symbol) {
        super("Asset name not found for request: " + symbol, HttpStatus.NOT_FOUND);
    }
}
