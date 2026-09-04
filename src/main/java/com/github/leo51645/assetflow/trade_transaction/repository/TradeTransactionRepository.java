package com.github.leo51645.assetflow.trade_transaction.repository;

import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransactionEntity, Long> {
    List<TradeTransactionEntity> findAllByUser_Id(Long userId);
    List<TradeTransactionEntity> findAllByInvestAsset_Id(Long investAssetId);

    void deleteAllByUser_Id(Long userId);
    void deleteAllByInvestAsset_Id(Long investAssetId);
}
