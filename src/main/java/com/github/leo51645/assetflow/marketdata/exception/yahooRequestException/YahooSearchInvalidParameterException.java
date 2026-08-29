package com.github.leo51645.assetflow.marketdata.exception.yahooRequestException;

public class YahooSearchInvalidParameterException extends RuntimeException {
    public YahooSearchInvalidParameterException(String requestParam) {
        super("Invalid search parameter: " + requestParam);
    }
}
