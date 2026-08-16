package com.assetflow.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPurchaseRequisitionRepository extends JpaRepository<PurchaseRequisitionJpaEntity, UUID> {
    Optional<PurchaseRequisitionJpaEntity> findByOrganizationIdAndId(UUID organizationId, UUID id);
    List<PurchaseRequisitionJpaEntity> findByOrganizationId(UUID organizationId);
    List<PurchaseRequisitionJpaEntity> findByOrganizationIdAndRequesterId(UUID organizationId, UUID requesterId);
    List<PurchaseRequisitionJpaEntity> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
