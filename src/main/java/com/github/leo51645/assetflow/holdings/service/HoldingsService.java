package com.github.leo51645.assetflow.holdings.service;

import com.github.leo51645.assetflow.holdings.domain.entity.HoldingId;
import com.github.leo51645.assetflow.holdings.domain.entity.HoldingsEntity;
import com.github.leo51645.assetflow.holdings.exception.HoldingNotFoundException;
import com.github.leo51645.assetflow.holdings.exception.InsufficientQuantityException;
import com.github.leo51645.assetflow.holdings.repository.HoldingsRepository;
import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TransactionType;
import com.github.leo51645.assetflow.trade_transaction.service.TradeTransactionService;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoldingsService {

    private final HoldingsRepository holdingsRepository;
    private final TradeTransactionService tradeTransactionService;

    @Transactional
    public HoldingsEntity createHolding(TradeTransactionEntity tradeTransactionEntity) {
        UserEntity userEntity = tradeTransactionEntity.getUser();
        InvestAssetEntity investAssetEntity = tradeTransactionEntity.getInvestAsset();

        HoldingsEntity holdingsEntity = HoldingsEntity.builder()
                .holdingId(new HoldingId(userEntity.getId(), investAssetEntity.getId()))
                .user(userEntity)
                .investAsset(investAssetEntity)
                .quantity(tradeTransactionEntity.getQuantity())
                .avgBuyPrice(tradeTransactionEntity.getExecutionPrice())
                .build();

        return holdingsRepository.save(holdingsEntity);
    }

    @Transactional(readOnly = true)
    public HoldingsEntity getHoldingById(HoldingId holdingId) {
        return holdingsRepository.findById(holdingId).orElseThrow(() -> new HoldingNotFoundException("Holding not found with id: " + holdingId));
    }

    @Transactional(readOnly = true)
    public List<HoldingsEntity> getHoldingsByUser(UserEntity userEntity) {
        return holdingsRepository.findAllByHoldingIdUserId(userEntity.getId());
    }

    @Transactional(readOnly = true)
    public List<HoldingsEntity> getHoldingsByInvestAsset(InvestAssetEntity investAssetEntity) {
        return holdingsRepository.findAllByHoldingIdAssetId(investAssetEntity.getId());
    }

    @Transactional
    public Optional<HoldingsEntity> updateHolding(HoldingsEntity oldHoldingsEntity, TradeTransactionEntity tradeTransactionEntity) {
        if (holdingsRepository.existsByHoldingId(oldHoldingsEntity.getHoldingId())) {

            Long oldQuantity = oldHoldingsEntity.getQuantity();
            Long requestedQuantity = tradeTransactionEntity.getQuantity();
            BigDecimal oldAvgBuyPrice = oldHoldingsEntity.getAvgBuyPrice();

            if (tradeTransactionEntity.getTransactionType() == TransactionType.BUY) {

                BigDecimal oldCost = oldAvgBuyPrice.multiply(BigDecimal.valueOf(oldQuantity));

                BigDecimal executionPrice = tradeTransactionEntity.getExecutionPrice();
                BigDecimal addedCost = executionPrice.multiply(BigDecimal.valueOf(requestedQuantity));

                long newQuantity = oldQuantity + requestedQuantity;

                BigDecimal newAvgBuyPrice = oldCost.add(addedCost).divide(BigDecimal.valueOf(newQuantity), 10, BigDecimal.ROUND_HALF_UP);

                HoldingsEntity updatedEntity = HoldingsEntity.builder()
                        .holdingId(oldHoldingsEntity.getHoldingId())
                        .user(oldHoldingsEntity.getUser())
                        .investAsset(oldHoldingsEntity.getInvestAsset())
                        .quantity(newQuantity)
                        .avgBuyPrice(newAvgBuyPrice)
                        .build();

                return Optional.of(holdingsRepository.save(updatedEntity));
            }
            else  {
                long newQuantity = oldQuantity - requestedQuantity;

                // when invalid quantity delete transaction entity and throw exception
                if (newQuantity < 1 && newQuantity != 0) {
                    tradeTransactionService.deleteTradeTransaction(tradeTransactionEntity);
                    throw new InsufficientQuantityException(newQuantity);

                    // when quantity = 0 delete holding from db
                } else if (newQuantity == 0) {
                    deleteHolding(oldHoldingsEntity);
                    return Optional.empty();

                    // when quantity >= 1 update entity in db
                } else {
                    HoldingsEntity updatedEntity = HoldingsEntity.builder()
                            .holdingId(oldHoldingsEntity.getHoldingId())
                            .user(oldHoldingsEntity.getUser())
                            .investAsset(oldHoldingsEntity.getInvestAsset())
                            .quantity(newQuantity)
                            .avgBuyPrice(oldAvgBuyPrice)
                            .build();

                    return Optional.of(holdingsRepository.save(updatedEntity));
                }
            }

        } else {
            throw new HoldingNotFoundException("Holding not found with id: " + oldHoldingsEntity.getHoldingId());
        }
    }

    @Transactional
    public void deleteHolding(HoldingsEntity holdingsEntity) {
        holdingsRepository.delete(holdingsEntity);
    }

    @Transactional
    public void deleteAllHoldingsByUserId(Long userId) {
        holdingsRepository.deleteAllByHoldingIdUserId(userId);
    }
}
