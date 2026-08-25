package com.github.leo51645.assetflow.marketdata.domain.dto;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataDtoMapper {

    public InvestAssetEntity toInvestAssetEntity(MarketDataYahooSearchResponseDto searchResponseDto, MarketDataYahooChartResponseDto chartResponseDto) {
        return InvestAssetEntity.builder()
                .id(null)
                .name(searchResponseDto.getName())
                .symbol(searchResponseDto.getSymbol())
                .currency(chartResponseDto.getCurrency())
                .assetType(searchResponseDto.getAssetType())
                .build();
    }
}
