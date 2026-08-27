package com.github.leo51645.assetflow.marketdata.exception;

import org.springframework.http.HttpStatus;

public class YahooSymbolMismatchException extends YahooApiException {
    public YahooSymbolMismatchException(String searchSymbol, String chartSymbol) {
        super("Search returned '" + searchSymbol + "' but Chart returned '" + chartSymbol + "'", HttpStatus.BAD_GATEWAY);
    }
}
