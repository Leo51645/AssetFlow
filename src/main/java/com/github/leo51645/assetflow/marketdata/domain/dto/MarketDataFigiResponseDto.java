package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataFigiResponseDto {
    private String name;
    private String isin;
    private String symbol;
    private AssetType assetType;
}
