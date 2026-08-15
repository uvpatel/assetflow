package com.assetflow.maintenance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TechnicianWorkloadRepository {
    TechnicianWorkload save(TechnicianWorkload workload);
    Optional<TechnicianWorkload> findByOrganizationIdAndTechnicianEmployeeId(UUID organizationId, UUID technicianEmployeeId);
    List<TechnicianWorkload> findAvailableTechnicians(UUID organizationId);
    List<TechnicianWorkload> findByOrganizationId(UUID organizationId);
}
