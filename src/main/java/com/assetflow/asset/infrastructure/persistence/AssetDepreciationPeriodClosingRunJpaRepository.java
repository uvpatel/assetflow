package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetDepreciationPeriodClosingRunJpaRepository extends JpaRepository<AssetDepreciationPeriodClosingRun, UUID> {
    Optional<AssetDepreciationPeriodClosingRun> findByOrganizationIdAndPeriodYearAndPeriodMonth(UUID organizationId, int periodYear, int periodMonth);
    List<AssetDepreciationPeriodClosingRun> findByOrganizationId(UUID organizationId);
}
