package com.assetflow.maintenance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetMaintenanceScheduleRepository {
    AssetMaintenanceSchedule save(AssetMaintenanceSchedule schedule);
    Optional<AssetMaintenanceSchedule> findById(UUID id);
    List<AssetMaintenanceSchedule> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
