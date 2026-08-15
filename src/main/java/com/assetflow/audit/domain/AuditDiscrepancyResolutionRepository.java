package com.assetflow.audit.domain;

import java.util.List;
import java.util.UUID;

public interface AuditDiscrepancyResolutionRepository {
    AuditDiscrepancyResolution save(AuditDiscrepancyResolution resolution);
    List<AuditDiscrepancyResolution> findByOrganizationIdAndAuditCycleId(UUID organizationId, UUID auditCycleId);
}
