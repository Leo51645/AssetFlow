package com.github.leo51645.assetflow.holdings.repository;

import com.github.leo51645.assetflow.holdings.domain.entity.HoldingId;
import com.github.leo51645.assetflow.holdings.domain.entity.HoldingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingsRepository extends JpaRepository<HoldingsEntity, HoldingId> {
    List<HoldingsEntity> findAllByHoldingIdUserId(Long userId);
    List<HoldingsEntity> findAllByHoldingIdAssetId(Long assetId);

    void deleteAllByHoldingIdUserId(Long userId);
}
