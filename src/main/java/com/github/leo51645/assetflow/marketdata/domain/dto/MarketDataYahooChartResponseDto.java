package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataYahooChartResponseDto {
    private String symbol;
    private BigDecimal currentPrice;
    private LocalDateTime priceUpdatedAt;
    private BigDecimal previousClosePrice;
    private Currency currency;
}
