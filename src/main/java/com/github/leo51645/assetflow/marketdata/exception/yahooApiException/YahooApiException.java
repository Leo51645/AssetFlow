package com.github.leo51645.assetflow.marketdata.exception.yahooApiException;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class YahooApiException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public YahooApiException(int statusCode) {
        super("Unexpected error while trying to connect to Yahoo finance with status code: " + statusCode);
        this.status = HttpStatus.valueOf(statusCode);
    }
    public YahooApiException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public YahooApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
