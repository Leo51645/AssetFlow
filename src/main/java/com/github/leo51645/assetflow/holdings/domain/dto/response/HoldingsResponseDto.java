package com.github.leo51645.assetflow.holdings.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record HoldingsResponseDto(String email, AssetData assetData) {
    public record AssetData(String assetName, String assetSymbol, long quantity, BigDecimal avgBuyPrice) {}
}
