package com.assetflow.maintenance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRequestRepository {
    MaintenanceRequest save(MaintenanceRequest request);
    Optional<MaintenanceRequest> findById(UUID id);
    List<MaintenanceRequest> findByOrganizationId(UUID organizationId);
}
