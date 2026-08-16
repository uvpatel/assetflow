package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetDepreciationPeriodClosingRunRepository {
    AssetDepreciationPeriodClosingRun save(AssetDepreciationPeriodClosingRun run);
    Optional<AssetDepreciationPeriodClosingRun> findByOrganizationIdAndPeriodYearAndPeriodMonth(UUID organizationId, int year, int month);
    List<AssetDepreciationPeriodClosingRun> findByOrganizationId(UUID organizationId);
}
