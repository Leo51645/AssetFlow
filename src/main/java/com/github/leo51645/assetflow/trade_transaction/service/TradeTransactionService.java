package com.github.leo51645.assetflow.trade_transaction.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.dto.request.OrderRequestDto;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TransactionType;
import com.github.leo51645.assetflow.trade_transaction.repository.TradeTransactionRepository;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeTransactionService {

    private final TradeTransactionRepository tradeTransactionRepository;

    public TradeTransactionEntity createTradeTransaction(InvestAssetEntity investAssetEntity, UserEntity userEntity,
                                                         OrderRequestDto orderRequestDto, TransactionType transactionType, BigDecimal realizedProfit) {
        TradeTransactionEntity tradeTransactionEntity = TradeTransactionEntity.builder()
                .investAsset(investAssetEntity)
                .user(userEntity)
                .transactionType(transactionType)
                .quantity(orderRequestDto.getQuantity())
                .executionPrice(investAssetEntity.getCurrentPrice())
                .totalAmount(BigDecimal.valueOf(orderRequestDto.getQuantity()).multiply(investAssetEntity.getCurrentPrice()))
                .executedAt(LocalDateTime.now())
                .realizedProfit(realizedProfit)
                .build();

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
