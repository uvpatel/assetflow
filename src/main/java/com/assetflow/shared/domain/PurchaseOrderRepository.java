package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder purchaseOrder);
    Optional<PurchaseOrder> findById(UUID id);
    List<PurchaseOrder> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId);
    List<PurchaseOrder> findByOrganizationId(UUID organizationId);
}
