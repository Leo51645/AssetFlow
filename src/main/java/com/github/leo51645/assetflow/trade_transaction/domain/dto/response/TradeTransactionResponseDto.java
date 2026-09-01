package com.github.leo51645.assetflow.trade_transaction.domain.dto.response;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeTransactionResponseDto {
    Long orderId;
    String assetName;
    String symbol;
    AssetType assetType;
    BigDecimal price;
    Long quantity;
}
