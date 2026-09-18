package com.github.leo51645.assetflow.trade_transaction.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.dto.request.OrderRequestDto;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TransactionType;
import com.github.leo51645.assetflow.trade_transaction.exception.TradeTransactionNotFoundException;
import com.github.leo51645.assetflow.trade_transaction.repository.TradeTransactionRepository;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeTransactionService {

    private final TradeTransactionRepository tradeTransactionRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public TradeTransactionEntity getTradeTransactionById(Long tradeTransactionId) {
        return tradeTransactionRepository.findById(tradeTransactionId)
                .orElseThrow(() -> new TradeTransactionNotFoundException("TradeTransaction not found with transactionId: " + tradeTransactionId));
    }

    @Transactional(readOnly = true)
    public List<TradeTransactionEntity> getAllTradeTransactionsByUserId(Long userId) {
        return tradeTransactionRepository.findAllByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<TradeTransactionEntity> getTradeTransactionsByInvestAssetId(Long investAssetId) {
        return tradeTransactionRepository.findAllByInvestAsset_Id(investAssetId);
    }

    @Transactional
    public void deleteTradeTransaction(TradeTransactionEntity tradeTransactionEntity) {
        tradeTransactionRepository.delete(tradeTransactionEntity);
    }

    @Transactional
    public void deleteAllTradeTransactionsByUserId(Long userId) {
        tradeTransactionRepository.deleteAllByUser_Id(userId);
    }

    @Transactional
    public void deleteAllTradeTransactionsByInvestAssetId(Long investAssetId) {
        tradeTransactionRepository.deleteAllByInvestAsset_Id(investAssetId);
    }

}
