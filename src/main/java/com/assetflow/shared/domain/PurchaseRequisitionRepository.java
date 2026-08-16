package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRequisitionRepository {
    PurchaseRequisition save(PurchaseRequisition requisition);
    Optional<PurchaseRequisition> findById(UUID id);
    Optional<PurchaseRequisition> findByOrganizationIdAndId(UUID organizationId, UUID id);
    List<PurchaseRequisition> findByOrganizationId(UUID organizationId);
    List<PurchaseRequisition> findByOrganizationIdAndRequesterId(UUID organizationId, UUID requesterId);
    List<PurchaseRequisition> findByOrganizationIdAndStatus(UUID organizationId, PurchaseRequisition.RequisitionStatus status);
}
