package com.github.leo51645.assetflow.investment_asset.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.exception.InvestAssetNotFoundException;
import com.github.leo51645.assetflow.investment_asset.repository.InvestAssetRepository;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataDtoMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.YahooSymbolMismatchException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestAssetServiceTest {

    @Mock
    private InvestAssetRepository investAssetRepository;

    @Mock
    private MarketDataDtoMapper marketDataDtoMapper;

    @InjectMocks
    private InvestAssetService investAssetService;

    MarketDataYahooSearchResponseDto searchResponseDto = MarketDataYahooSearchResponseDto.builder()
            .name("Apple Inc.")
            .symbol("APPL")
            .assetType(AssetType.STOCK)
            .build();
    MarketDataYahooChartResponseDto chartResponseDto = MarketDataYahooChartResponseDto.builder()
            .symbol("APPL")
            .currentPrice(BigDecimal.valueOf(221.23))
            .priceUpdatedAt(LocalDateTime.now())
            .previousClosePrice(BigDecimal.valueOf(118.54))
            .currency(Currency.USD)
            .build();

    @Nested
    class SaveInvestAsset {
        @Test
        void shouldSaveNewEntityInDb() {
            InvestAssetEntity investAssetEntity = new InvestAssetEntity();
            InvestAssetEntity expected = InvestAssetEntity.builder()
                    .name(searchResponseDto.getName())
                    .symbol(searchResponseDto.getSymbol())
                    .currency(chartResponseDto.getCurrency())
                    .assetType(searchResponseDto.getAssetType())
                    .currentPrice(chartResponseDto.getCurrentPrice())
                    .priceUpdatedAt(chartResponseDto.getPriceUpdatedAt())
                    .build();

            when(marketDataDtoMapper.toInvestAssetEntity(searchResponseDto, chartResponseDto)).thenReturn(investAssetEntity);
            when(investAssetRepository.findBySymbol(any())).thenReturn(Optional.empty());
            when(investAssetRepository.save(investAssetEntity)).thenReturn(expected);

            InvestAssetEntity actual = investAssetService.createInvestAsset(searchResponseDto, chartResponseDto);

            assertEquals(expected, actual);
            verify(marketDataDtoMapper).toInvestAssetEntity(searchResponseDto, chartResponseDto);
            verify(investAssetRepository).findBySymbol(searchResponseDto.getSymbol());
            verify(investAssetRepository).save(investAssetEntity);
        }

        @Test
        void shouldNotSaveNewEntityDueToMismatchOfSymbols() {
            chartResponseDto.setSymbol("GOOG");

            assertThrows(YahooSymbolMismatchException.class, () -> investAssetService.createInvestAsset(searchResponseDto, chartResponseDto));
            verify(marketDataDtoMapper, never()).toInvestAssetEntity(any(), any());
            verify(investAssetRepository, never()).save(any(InvestAssetEntity.class));
        }

        @Test
        void shouldNotSaveNewEntityDueToSymbolAlreadyExists() {
            InvestAssetEntity investAssetEntity = new InvestAssetEntity();
            InvestAssetEntity expected = InvestAssetEntity.builder().name(searchResponseDto.getName()).build();

            when(marketDataDtoMapper.toInvestAssetEntity(searchResponseDto, chartResponseDto)).thenReturn(investAssetEntity);
            when(investAssetRepository.findBySymbol(any())).thenReturn(Optional.of(expected));

            InvestAssetEntity actual = investAssetService.createInvestAsset(searchResponseDto, chartResponseDto);

            assertEquals(expected, actual);
            verify(marketDataDtoMapper).toInvestAssetEntity(searchResponseDto, chartResponseDto);
            verify(investAssetRepository, never()).save(any(InvestAssetEntity.class));
        }
    }

    @Nested
    class GetInvestAsset {
        @Test
        void shouldReturnEntityBySymbol() {
            InvestAssetEntity expected = InvestAssetEntity.builder().symbol(searchResponseDto.getSymbol()).build();

            when(investAssetRepository.findBySymbol(anyString())).thenReturn(Optional.of(expected));

            Optional<InvestAssetEntity> actual = investAssetService.getInvestAssetBySymbol(searchResponseDto.getSymbol());

            assertTrue(actual.isPresent());
            assertEquals(expected, actual.get());
            verify(investAssetRepository).findBySymbol(searchResponseDto.getSymbol());
        }

        @Test
        void shouldReturnOptionalEmptyDueToSymbolNotFound() {
            when(investAssetRepository.findBySymbol(anyString())).thenReturn(Optional.empty());

            Optional<InvestAssetEntity> actual = investAssetService.getInvestAssetBySymbol(chartResponseDto.getSymbol());

            assertTrue(actual.isEmpty());
            verify(investAssetRepository).findBySymbol(chartResponseDto.getSymbol());
        }

        @Test
        void shouldReturnEntityById() {
            InvestAssetEntity expected = InvestAssetEntity.builder().id(99L).build();

            when(investAssetRepository.findById(anyLong())).thenReturn(Optional.of(expected));

            Optional<InvestAssetEntity> actual = investAssetService.getInvestAssetById(99L);

            assertTrue(actual.isPresent());
            assertEquals(expected, actual.get());
            verify(investAssetRepository).findById(99L);
        }

        @Test
        void shouldReturnOptionalEmptyDueToIdNotFound() {
            when(investAssetRepository.findById(anyLong())).thenReturn(Optional.empty());

            Optional<InvestAssetEntity> actual = investAssetService.getInvestAssetById(99L);

            assertTrue(actual.isEmpty());
            verify(investAssetRepository).findById(99L);
        }
    }

    @Nested
    class DeleteInvestAsset {
        @Test
        void shouldDeleteInvestAsset() {
            InvestAssetEntity investAssetEntity = new InvestAssetEntity();

            investAssetService.deleteInvestAsset(investAssetEntity);

            verify(investAssetRepository).delete(investAssetEntity);
        }

        @Test
        void shouldDeleteInvestAssetBySymbol() {
            InvestAssetEntity investAssetEntity = InvestAssetEntity.builder().id(99L).build();
            when(investAssetRepository.findBySymbol(anyString())).thenReturn(Optional.of(investAssetEntity));

            investAssetService.deleteInvestAssetBySymbol(searchResponseDto.getSymbol());

            verify(investAssetRepository).findBySymbol(anyString());
            verify(investAssetRepository).delete(investAssetEntity);
        }

        @Test
        void shouldNotDeleteInvestAssetBySymbolDueToInvestAssetNotFound() {
            when(investAssetRepository.findBySymbol(anyString())).thenReturn(Optional.empty());

            assertThrows(InvestAssetNotFoundException.class, () -> investAssetService.deleteInvestAssetBySymbol(chartResponseDto.getSymbol()));
            verify(investAssetRepository, never()).delete(any(InvestAssetEntity.class));
        }
    }




}