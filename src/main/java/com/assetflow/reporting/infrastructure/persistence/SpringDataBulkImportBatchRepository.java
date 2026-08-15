package com.assetflow.reporting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataBulkImportBatchRepository extends JpaRepository<BulkImportBatchJpaEntity, UUID> {
    List<BulkImportBatchJpaEntity> findByOrganizationId(UUID organizationId);
}
