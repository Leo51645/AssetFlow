package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataDtoMapper {

    public InvestAssetEntity toInvestAssetEntity(MarketDataFigiResponseDto figiResponseDto, MarketDataYahooResponseDto yahooResponseDto) {
        return InvestAssetEntity.builder()
                .id(null)
                .name(figiResponseDto.getName())
                .isin(figiResponseDto.getIsin())
                .symbol(figiResponseDto.getSymbol())
                .currency(yahooResponseDto.getCurrency())
                .assetType(figiResponseDto.getAssetType())
                .build();
    }
}
