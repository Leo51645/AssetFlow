package com.github.leo51645.assetflow.investment_asset.repository;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestAssetRepository extends JpaRepository<InvestAssetEntity, Long> {

    Optional<InvestAssetEntity> findByIsin(String isin);

    boolean existsByIsin(String isin);

    void deleteByIsin(String isin);
}
