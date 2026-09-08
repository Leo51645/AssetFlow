package com.github.leo51645.assetflow.trade_transaction.service;

import com.github.leo51645.assetflow.trade_transaction.domain.entity.TradeTransactionEntity;
import com.github.leo51645.assetflow.trade_transaction.repository.TradeTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        TradeTransactionEntity expected = new TradeTransactionEntity();
        when(tradeTransactionRepository.save(any(TradeTransactionEntity.class))).thenReturn(expected);

        TradeTransactionEntity actual = tradeTransactionService.saveTradeTransaction(expected);

        assertEquals(expected, actual);
        verify(tradeTransactionRepository).save(expected);
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