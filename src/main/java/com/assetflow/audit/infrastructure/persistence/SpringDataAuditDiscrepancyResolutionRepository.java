package com.assetflow.audit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAuditDiscrepancyResolutionRepository extends JpaRepository<AuditDiscrepancyResolutionJpaEntity, UUID> {
    List<AuditDiscrepancyResolutionJpaEntity> findByOrganizationIdAndAuditCycleId(UUID organizationId, UUID auditCycleId);
}
