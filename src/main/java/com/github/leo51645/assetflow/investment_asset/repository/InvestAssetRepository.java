package com.github.leo51645.assetflow.investment_asset.repository;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestAssetRepository extends JpaRepository<InvestAssetEntity, Long> {

    Optional<InvestAssetEntity> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    @Query("SELECT a FROM InvestAssetEntity a where a.priceUpdatedAt <= :updatedBefore")
    List<InvestAssetEntity> findOutdatedAssets(@Param("updatedBefore") LocalDateTime updatedBefore);
}
