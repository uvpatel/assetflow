package com.assetflow.maintenance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceSlaPolicyRepository {
    MaintenanceSlaPolicy save(MaintenanceSlaPolicy policy);
    Optional<MaintenanceSlaPolicy> findByOrganizationIdAndPriority(UUID organizationId, MaintenanceSlaPolicy.Priority priority);
    List<MaintenanceSlaPolicy> findByOrganizationId(UUID organizationId);
}
