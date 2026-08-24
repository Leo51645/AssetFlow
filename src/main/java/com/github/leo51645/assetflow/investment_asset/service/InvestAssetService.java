package com.github.leo51645.assetflow.investment_asset.service;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.exception.AssetNotFoundException;
import com.github.leo51645.assetflow.investment_asset.repository.InvestAssetRepository;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataDtoMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataResponseDto;
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
    public InvestAssetEntity saveInvestAsset(MarketDataResponseDto marketDataResponseDto) {
        if (investAssetRepository.existsByIsin(marketDataResponseDto.getIsin())) {
            return findInvestAssetByIsin(marketDataResponseDto.getIsin()).orElseThrow(
                    () -> new AssetNotFoundException("Asset with ISIN " + marketDataResponseDto.getIsin() + " not found"));
        }

        InvestAssetEntity investAssetEntity = marketDataDtoMapper.toInvestAssetEntity(marketDataResponseDto);

        return investAssetRepository.save(investAssetEntity);
    }

    @Transactional(readOnly = true)
    public Optional<InvestAssetEntity> findInvestAssetByIsin(String isin) {
        return investAssetRepository.findByIsin(isin);
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
    public void deleteInvestAssetByIsin(String isin) {
        InvestAssetEntity investAssetEntity = findInvestAssetByIsin(isin).orElseThrow(() -> new AssetNotFoundException("Asset with ISIN " + isin + " not found"));
        investAssetRepository.delete(investAssetEntity);
    }
}
