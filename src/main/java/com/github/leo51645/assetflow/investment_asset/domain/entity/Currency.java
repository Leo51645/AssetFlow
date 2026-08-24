package com.github.leo51645.assetflow.investment_asset.domain.entity;


public enum Currency {
    EUR("Euro"),
    USD("US Dollar"),
    GBP("British Pound"),
    CHF("Swiss Franc"),
    JPY("Japanese Yen"),
    CNY("Chinese Yuan");

    private final String englishName;
    Currency(String englishName) {
        this.englishName = englishName;
    }
}
