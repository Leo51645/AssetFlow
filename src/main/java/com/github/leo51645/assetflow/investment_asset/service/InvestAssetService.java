package com.github.leo51645.assetflow.investment_asset.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.exception.AssetNotFoundException;
import com.github.leo51645.assetflow.investment_asset.repository.InvestAssetRepository;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataDtoMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
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
    public InvestAssetEntity saveInvestAsset(MarketDataYahooSearchResponseDto searchResponseDto, MarketDataYahooChartResponseDto chartResponseDto) {
        InvestAssetEntity investAssetEntity = marketDataDtoMapper.toInvestAssetEntity(searchResponseDto, chartResponseDto);

        return findInvestAssetBySymbol(searchResponseDto.getSymbol()).orElseGet(() -> investAssetRepository.save(investAssetEntity));
    }

    @Transactional(readOnly = true)
    public Optional<InvestAssetEntity> findInvestAssetBySymbol(String symbol) {
        return investAssetRepository.findBySymbol(symbol);
    }

    @Transactional(readOnly = true)
    public Optional<InvestAssetEntity> findInvestAssetById(long id) {
        return investAssetRepository.findById(id);
    }

    @Transactional
    public void deleteInvestAsset(InvestAssetEntity investAssetEntity) {
        investAssetRepository.delete(investAssetEntity);
    }

    @Transactional
    public void deleteInvestAssetBySymbol(String symbol) {
        InvestAssetEntity investAssetEntity = findInvestAssetBySymbol(symbol).orElseThrow(() -> new AssetNotFoundException("Asset with Symbol " + symbol + " not found"));
        investAssetRepository.delete(investAssetEntity);
    }
}
