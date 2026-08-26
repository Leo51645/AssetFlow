package com.github.leo51645.assetflow.marketdata.exception;

import org.springframework.http.HttpStatus;

public class YahooMarketDataUnavailableException extends YahooApiException {
    public YahooMarketDataUnavailableException(String symbol) {
        super("Missing market data for Symbol: " + symbol, HttpStatus.BAD_GATEWAY);
    }
}
