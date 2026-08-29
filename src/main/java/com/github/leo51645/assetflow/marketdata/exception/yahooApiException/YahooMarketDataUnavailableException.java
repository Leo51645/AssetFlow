package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooMarketDataUnavailableException extends YahooApiException {
    public YahooMarketDataUnavailableException(String requestParam) {
        super("Missing market data for request parameter: " + requestParam, HttpStatus.BAD_GATEWAY);
    }
}
