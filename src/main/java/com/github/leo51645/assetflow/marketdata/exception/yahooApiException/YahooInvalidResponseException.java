package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import org.springframework.http.HttpStatus;

public class YahooInvalidResponseException extends YahooApiException {
    public YahooInvalidResponseException(String requestParam) {
        super("Invalid JSON response body with request parameter: " + requestParam, HttpStatus.BAD_GATEWAY);
    }
}
