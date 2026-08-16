package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRequisitionItemRepository {
    PurchaseRequisitionItem save(PurchaseRequisitionItem item);
    Optional<PurchaseRequisitionItem> findById(UUID id);
    List<PurchaseRequisitionItem> findByRequisitionId(UUID requisitionId);
    void deleteById(UUID id);
}
