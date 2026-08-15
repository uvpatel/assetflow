package com.assetflow.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataVendorPerformanceEvaluationRepository extends JpaRepository<VendorPerformanceEvaluationJpaEntity, UUID> {
    List<VendorPerformanceEvaluationJpaEntity> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId);
}
