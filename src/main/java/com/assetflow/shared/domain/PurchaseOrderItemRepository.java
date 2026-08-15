package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderItemRepository {
    PurchaseOrderItem save(PurchaseOrderItem item);
    Optional<PurchaseOrderItem> findById(UUID id);
    List<PurchaseOrderItem> findByPurchaseOrderId(UUID purchaseOrderId);
}
