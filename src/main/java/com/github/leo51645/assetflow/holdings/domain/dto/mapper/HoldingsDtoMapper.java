package com.github.leo51645.assetflow.holdings.domain.dto.mapper;

import com.github.leo51645.assetflow.holdings.domain.dto.response.HoldingsResponseDto;
import com.github.leo51645.assetflow.holdings.domain.entity.HoldingsEntity;
import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HoldingsDtoMapper {

    public HoldingsResponseDto toHoldingsResponseDto(List<HoldingsEntity> holdingsEntities,
                                                     UserEntity userEntity) {
        List<HoldingsResponseDto.AssetData> assetDataList = holdingsEntities.stream()
                .map(holding -> new HoldingsResponseDto.AssetData(
                        holding.getInvestAsset().getName(),
                        holding.getInvestAsset().getSymbol(),
                        holding.getQuantity(),
                        holding.getAvgBuyPrice()
                )).toList();

        return HoldingsResponseDto.builder()
                .email(userEntity.getEmail())
                .assetData(assetDataList)
                .build();
    }
}
