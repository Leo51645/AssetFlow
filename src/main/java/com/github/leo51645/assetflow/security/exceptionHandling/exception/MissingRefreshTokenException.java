package com.github.leo51645.assetflow.security.exceptionHandling.exception;

public class MissingRefreshTokenException extends RuntimeException {
    public MissingRefreshTokenException(String message) {
        super(message);
    }
}
