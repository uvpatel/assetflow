package com.assetflow.maintenance.domain;

import java.util.List;
import java.util.UUID;

public interface MaintenancePartItemRepository {
    MaintenancePartItem save(MaintenancePartItem partItem);
    List<MaintenancePartItem> findByOrganizationIdAndMaintenanceId(UUID organizationId, UUID maintenanceId);
}
