package com.assetflow.shared.application;

import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderUseCase {

    private final PurchaseOrderRepository repository;

    public PurchaseOrderUseCase(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(
            UUID organizationId,
            UUID vendorId,
            String poNumber,
            BigDecimal initialTotal) {
        PurchaseOrder po = PurchaseOrder.create(organizationId, vendorId, poNumber, initialTotal);
        return repository.save(po);
    }

    @Transactional
    public PurchaseOrder issuePurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + poId));
        po.issue();
        return repository.save(po);
    }

    @Transactional
    public PurchaseOrder fulfillPurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + poId));
        po.fulfill();
        return repository.save(po);
    }

    @Transactional
    public PurchaseOrder cancelPurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + poId));
        po.cancel();
        return repository.save(po);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getPurchaseOrdersForVendor(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
    }
}
