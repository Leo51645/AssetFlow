package com.github.leo51645.assetflow.trade_transaction.service;

import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.repository.TradeTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeTransactionService {

    private final TradeTransactionRepository tradeTransactionRepository;

    public TradeTransactionEntity saveTradeTransaction(TradeTransactionEntity tradeTransactionEntity) {
        return tradeTransactionRepository.save(tradeTransactionEntity);
    }

    public Optional<TradeTransactionEntity> getTradeTransactionById(Long tradeTransactionId) {
        return tradeTransactionRepository.findById(tradeTransactionId);
    }

    public List<TradeTransactionEntity> getAllTradeTransactionsByUserId(Long userId) {
        return tradeTransactionRepository.findAllByUser_Id(userId);
    }

    public List<TradeTransactionEntity> getTradeTransactionsByInvestAssetId(Long investAssetId) {
        return tradeTransactionRepository.findAllByInvestAsset_Id(investAssetId);
    }

    public void deleteTradeTransactionByTradeTransactionId(Long tradeTransactionId) {
        tradeTransactionRepository.deleteById(tradeTransactionId);
    }

    public void deleteAllTradeTransactionsByUserId(Long userId) {
        tradeTransactionRepository.deleteAllByUser_Id(userId);
    }

    public void deleteAllTradeTransactionsByInvestAssetId(Long investAssetId) {
        tradeTransactionRepository.deleteAllByInvestAsset_Id(investAssetId);
    }

}
