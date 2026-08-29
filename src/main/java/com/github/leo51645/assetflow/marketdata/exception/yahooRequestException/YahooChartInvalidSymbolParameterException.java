package com.github.leo51645.assetflow.marketdata.exception.yahooRequestException;

public class YahooChartInvalidSymbolParameterException extends RuntimeException {
    public YahooChartInvalidSymbolParameterException(String symbol) {
        super("Invalid chart symbol for http chart request: " + symbol);
    }
}
