package com.assetflow.audit.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditItemRepository {
    AuditItem save(AuditItem item);
    List<AuditItem> saveAll(List<AuditItem> items);
    Optional<AuditItem> findById(UUID id);
    List<AuditItem> findByAuditCycleId(UUID auditCycleId);
}
