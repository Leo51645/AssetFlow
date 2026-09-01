package com.github.leo51645.assetflow.trade_transaction.repository;

import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeTransactionRepository extends JpaRepository<TradeTransactionEntity, Long> {
    List<TradeTransactionEntity> findAllByUser_Id(Long userId);
    List<TradeTransactionEntity> findAllByInvestAsset_Id(Long investAssetId);
}
