package com.github.leo51645.assetflow.marketdata.util;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
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

    public String createYahooFinanceSearchRequestURI(String requestParam) {
        return "https://query1.finance.yahoo.com/v1/finance/search?q=" + requestParam;
    }

    public boolean isIsin(String requestParam) {
        return requestParam.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$");
    }
}
