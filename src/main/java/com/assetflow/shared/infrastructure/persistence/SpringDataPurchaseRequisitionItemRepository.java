package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.PurchaseRequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataPurchaseRequisitionItemRepository extends JpaRepository<PurchaseRequisitionItem, UUID> {
    List<PurchaseRequisitionItem> findByRequisitionId(UUID requisitionId);
}
