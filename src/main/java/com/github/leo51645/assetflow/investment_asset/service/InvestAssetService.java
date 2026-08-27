package com.github.leo51645.assetflow.investment_asset.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.exception.InvestAssetNotFoundException;
import com.github.leo51645.assetflow.investment_asset.repository.InvestAssetRepository;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataDtoMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.YahooSymbolMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvestAssetService {

    private final InvestAssetRepository investAssetRepository;
    private final MarketDataDtoMapper marketDataDtoMapper;

    @Transactional
    public InvestAssetEntity createInvestAsset(MarketDataYahooSearchResponseDto searchResponseDto, MarketDataYahooChartResponseDto chartResponseDto) {
        String searchSymbol = searchResponseDto.getSymbol();
        String chartSymbol = chartResponseDto.getSymbol();

        if (!searchSymbol.equals(chartSymbol)) {
            throw new YahooSymbolMismatchException(searchSymbol, chartSymbol);
        }

        InvestAssetEntity investAssetEntity = marketDataDtoMapper.toInvestAssetEntity(searchResponseDto, chartResponseDto);

        return getInvestAssetBySymbol(searchResponseDto.getSymbol()).orElseGet(() -> investAssetRepository.save(investAssetEntity));
    }

    @Transactional(readOnly = true)
    public Optional<InvestAssetEntity> getInvestAssetBySymbol(String symbol) {
        return investAssetRepository.findBySymbol(symbol);
    }

    @Transactional(readOnly = true)
    public Optional<InvestAssetEntity> getInvestAssetById(long id) {
        return investAssetRepository.findById(id);
    }

    @Transactional
    public void deleteInvestAsset(InvestAssetEntity investAssetEntity) {
        investAssetRepository.delete(investAssetEntity);
    }

    @Transactional
    public void deleteInvestAssetBySymbol(String symbol) {
        InvestAssetEntity investAssetEntity = getInvestAssetBySymbol(symbol).orElseThrow(() -> new InvestAssetNotFoundException(symbol));
        investAssetRepository.delete(investAssetEntity);
    }
}
