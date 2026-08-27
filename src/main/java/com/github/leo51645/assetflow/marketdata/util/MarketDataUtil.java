package com.github.leo51645.assetflow.marketdata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import org.springframework.stereotype.Component;

@Component
public class MarketDataUtil {

    public AssetType getAssetType(String quoteType) {
        AssetType assetType = AssetType.OTHER;
        switch (quoteType) {
            case "ETF" -> assetType = AssetType.ETF;
            case "EQUITY" -> assetType = AssetType.STOCK;
            case "FUTURE" -> assetType = AssetType.COMMODITY;
            case "CRYPTOCURRENCY" -> assetType = AssetType.CRYPTO;
        }

        return assetType;
    }

    public Currency getCurrency(String currencyString) {
        Currency currency = Currency.USD;

        if (currencyString == null || currencyString.isEmpty()) {
            return currency;
        }

        switch (currencyString) {
            case "EUR" -> currency = Currency.EUR;
            case "GBP" -> currency = Currency.GBP;
            case "CHF" -> currency = Currency.CHF;
            case "JPY" -> currency = Currency.JPY;
            case "CNY" -> currency = Currency.CNY;
        }

        return currency;
    }

    public String createYahooFinanceSearchRequestURI(String requestParam) {
        return "https://query1.finance.yahoo.com/v1/finance/search?q=" + requestParam;
    }

    public String createYahooFinanceChartRequestURI(String symbol) {
        return "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?range=1d&interval=1d";
    }

    public boolean isIsin(String requestParam) {
        return requestParam.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$");
    }

    public MarketDataYahooSearchResponseDto getMarketDataFromJsonNode(JsonNode asset) {
        MarketDataYahooSearchResponseDto parsedAsset = new MarketDataYahooSearchResponseDto();

        String name = asset.get("longname").asText();
        parsedAsset.setName(name);

        String symbol = asset.get("symbol").asText();
        parsedAsset.setSymbol(symbol);

        String stringAssetType = asset.get("quoteType").asText();
        parsedAsset.setAssetType(getAssetType(stringAssetType));

        return parsedAsset;
    }
}
