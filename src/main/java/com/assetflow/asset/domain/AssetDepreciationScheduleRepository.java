package com.assetflow.asset.domain;

import java.util.Optional;
import java.util.UUID;

public interface AssetDepreciationScheduleRepository {
    AssetDepreciationSchedule save(AssetDepreciationSchedule schedule);
    Optional<AssetDepreciationSchedule> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
