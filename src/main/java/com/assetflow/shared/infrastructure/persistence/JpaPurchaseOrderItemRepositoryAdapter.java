package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.PurchaseOrderItem;
import com.assetflow.shared.domain.PurchaseOrderItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPurchaseOrderItemRepositoryAdapter implements PurchaseOrderItemRepository {

    private final PurchaseOrderItemJpaRepository jpaRepository;

    public JpaPurchaseOrderItemRepositoryAdapter(PurchaseOrderItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PurchaseOrderItem save(PurchaseOrderItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public Optional<PurchaseOrderItem> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PurchaseOrderItem> findByPurchaseOrderId(UUID purchaseOrderId) {
        return jpaRepository.findByPurchaseOrderId(purchaseOrderId);
    }
}
