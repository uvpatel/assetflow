package com.assetflow.audit.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditCycleRepository {
    AuditCycle save(AuditCycle auditCycle);
    Optional<AuditCycle> findById(UUID id);
    List<AuditCycle> findByOrganizationId(UUID organizationId);
}
