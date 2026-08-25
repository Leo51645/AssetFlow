package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataYahooChartResponseDto {
    private Currency currency;
}
