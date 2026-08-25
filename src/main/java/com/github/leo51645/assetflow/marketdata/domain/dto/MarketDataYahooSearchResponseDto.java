package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataYahooSearchResponseDto {
    private String name;
    private String symbol;
    private AssetType assetType;
}
