package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketDataDtoMapper {

    public MarketDataResponseDto toMarketDataDto(String raw) {
        return null;
    }

    public InvestAssetEntity toInvestAssetEntity(MarketDataResponseDto marketDataResponseDto) {
        return InvestAssetEntity.builder()
                .id(null)
                .name(marketDataResponseDto.getName())
                .isin(marketDataResponseDto.getIsin())
                .symbol(marketDataResponseDto.getSymbol())
                .currency(marketDataResponseDto.getCurrency())
                .assetType(marketDataResponseDto.getAssetType())
                .build();
    }
}
