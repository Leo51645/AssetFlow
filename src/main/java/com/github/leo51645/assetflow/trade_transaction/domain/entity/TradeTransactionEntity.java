package com.github.leo51645.assetflow.trade_transaction.domain.entity;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trade_transactions")
public class TradeTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investAsset_id", nullable = false)
    private InvestAssetEntity investAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType transactionType;

    @Column(nullable = false, updatable = false)
    private Long quantity;

    @Column(nullable = false, updatable = false)
    private BigDecimal executionPrice;

    @Column(nullable = false, updatable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime executedAt;

    @Column(updatable = false)
    private BigDecimal realizedProfit;
}
