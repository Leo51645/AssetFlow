package com.github.leo51645.assetflow.holdings.exception;

public class InsufficientQuantityException extends RuntimeException {
    public InsufficientQuantityException(long quantity) {
        super("Insufficient quantity at sell order: " + quantity);
    }
}
