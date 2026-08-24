package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataResponseDto {
    private String name;
    private String isin;
    private String symbol;
    private Currency currency;
    private AssetType assetType;
}
