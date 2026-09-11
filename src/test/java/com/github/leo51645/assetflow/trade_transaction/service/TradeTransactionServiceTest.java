package com.github.leo51645.assetflow.trade_transaction.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.dto.request.OrderRequestDto;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.domain.entity.TransactionType;
import com.github.leo51645.assetflow.trade_transaction.repository.TradeTransactionRepository;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeTransactionServiceTest {

    @Mock
    private TradeTransactionRepository tradeTransactionRepository;

    @InjectMocks
    private TradeTransactionService tradeTransactionService;

    @Test
    void shouldSaveTradeTransaction() {
        InvestAssetEntity investAssetEntity = InvestAssetEntity.builder().currentPrice(BigDecimal.valueOf(6)).build();
        UserEntity userEntity = new UserEntity();
        OrderRequestDto orderRequestDto = new OrderRequestDto("someAsset", 5L);
        TransactionType transactionType = TransactionType.BUY;

        when(tradeTransactionRepository.save(any(TradeTransactionEntity.class))).thenAnswer(i -> i.getArgument(0));

        TradeTransactionEntity actual = tradeTransactionService.createTradeTransaction(
                investAssetEntity,
                userEntity,
                orderRequestDto,
                transactionType,
                null);

        assertThat(actual.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertEquals(BigDecimal.valueOf(6), actual.getExecutionPrice());
        assertEquals(5L, actual.getQuantity());
        assertEquals(transactionType, actual.getTransactionType());
        assertNull(actual.getRealizedProfit());
        assertEquals(userEntity, actual.getUser());
        assertEquals(investAssetEntity, actual.getInvestAsset());
        verify(tradeTransactionRepository).save(any(TradeTransactionEntity.class));
    }

    @Test
    void shouldGetTradeTransactionById() {
        TradeTransactionEntity expected = new TradeTransactionEntity();
        when(tradeTransactionRepository.findById(anyLong())).thenReturn(Optional.of(expected));

        Optional<TradeTransactionEntity> actual = tradeTransactionService.getTradeTransactionById(99L);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void shouldGetEmptyTradeTransactionById() {
        Optional<TradeTransactionEntity> expected = Optional.empty();

        when(tradeTransactionRepository.findById(anyLong())).thenReturn(expected);
        Optional<TradeTransactionEntity> actual = tradeTransactionService.getTradeTransactionById(99L);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldGetAllTradeTransactionByUserId() {
        List<TradeTransactionEntity> expected = new ArrayList<>();
        expected.add(new TradeTransactionEntity());
        expected.add(new TradeTransactionEntity());
        when(tradeTransactionRepository.findAllByUser_Id(anyLong())).thenReturn(expected);

        List<TradeTransactionEntity> actual = tradeTransactionService.getAllTradeTransactionsByUserId(99L);

        assertEquals(expected, actual);
    }

    @Test
    void shouldGetAllTradeTransactionByInvestAssetId() {
        List<TradeTransactionEntity> expected = new ArrayList<>();
        expected.add(new TradeTransactionEntity());
        expected.add(new TradeTransactionEntity());
        when(tradeTransactionRepository.findAllByInvestAsset_Id(anyLong())).thenReturn(expected);

        List<TradeTransactionEntity> actual = tradeTransactionService.getTradeTransactionsByInvestAssetId(99L);

        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteTradeTransactionByTradeTransactionId() {
        tradeTransactionService.deleteTradeTransactionByTradeTransactionId(99L);

        verify(tradeTransactionRepository).deleteById(99L);
    }

    @Test
    void shouldDeleteAllTradeTransactionsByUserId() {
        tradeTransactionService.deleteAllTradeTransactionsByUserId(99L);

        verify(tradeTransactionRepository).deleteAllByUser_Id(99L);
    }

    @Test
    void shouldDeleteAllTradeTransactionsByInvestAssetId() {
        tradeTransactionService.deleteAllTradeTransactionsByInvestAssetId(99L);
        verify(tradeTransactionRepository).deleteAllByInvestAsset_Id(99L);
    }
}