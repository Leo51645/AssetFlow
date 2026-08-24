package com.github.leo51645.assetflow.investment_asset.repository;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestAssetRepository extends JpaRepository<InvestAssetEntity, Long> {

    Optional<InvestAssetEntity> findByIsin(String isin);
    Optional<InvestAssetEntity> findByName(String name);

    boolean existsByIsin(String isin);
    boolean existsByName(String name);
}
