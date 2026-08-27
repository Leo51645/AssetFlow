package com.github.leo51645.assetflow.investment_asset.exception;

public class InvestAssetNotFoundException extends RuntimeException {
    public InvestAssetNotFoundException(String symbol) {
        super("Asset not found with symbol: " + symbol);
    }
}
