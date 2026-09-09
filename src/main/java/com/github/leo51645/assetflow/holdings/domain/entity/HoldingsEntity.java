package com.github.leo51645.assetflow.holdings.domain.entity;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "holdings")
public class HoldingsEntity {

    @EmbeddedId
    private HoldingId holdingId;

    @ManyToOne
    @MapsId("userId") // connects holdingId.userId with this field
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("assetId")
    @JoinColumn(name = "invest_asset_id")
    private InvestAssetEntity investAsset;

    @Setter
    @Column(nullable = false)
    private BigDecimal quantity;

    @Setter
    @Column(nullable = false)
    private BigDecimal avgBuyPrice;

}
