package com.assetflow.shared.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseOrderItem;
import com.assetflow.shared.domain.PurchaseOrderItemRepository;
import com.assetflow.shared.domain.PurchaseOrderRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderUseCase.class);

    private final PurchaseOrderRepository repository;
    private final PurchaseOrderItemRepository itemRepository;
    private final AssetRepository assetRepository;

    public PurchaseOrderUseCase(PurchaseOrderRepository repository,
                                PurchaseOrderItemRepository itemRepository,
                                AssetRepository assetRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.assetRepository = assetRepository;
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
    public PurchaseOrderItem addPurchaseOrderItem(
            UUID organizationId,
            UUID purchaseOrderId,
            String itemDescription,
            UUID categoryId,
            BigDecimal unitPrice,
            int quantityOrdered) {
        PurchaseOrder po = repository.findById(purchaseOrderId)
                .orElseThrow(() -> new DomainViolationException("Purchase Order Not Found", "Target PO was not found."));

        if (!po.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot modify PO belonging to another organization.");
        }

        if (po.getStatus() != PurchaseOrder.Status.DRAFT) {
            throw new DomainViolationException("Invalid Operation", "Items can only be added to DRAFT purchase orders.");
        }

        PurchaseOrderItem item = new PurchaseOrderItem(organizationId, purchaseOrderId, itemDescription, categoryId, unitPrice, quantityOrdered);
        PurchaseOrderItem savedItem = itemRepository.save(item);

        List<PurchaseOrderItem> allItems = itemRepository.findByPurchaseOrderId(purchaseOrderId);
        BigDecimal newTotal = allItems.stream().map(PurchaseOrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        po.updateTotalAmount(newTotal);
        repository.save(po);

        log.info("Added PO item {} (Qty: {}) to PO {}", itemDescription, quantityOrdered, purchaseOrderId);
        return savedItem;
    }

    @Transactional
    public List<Asset> receivePurchaseOrderItem(UUID organizationId, UUID purchaseOrderId, UUID itemId, int quantityReceived) {
        PurchaseOrder po = repository.findById(purchaseOrderId)
                .orElseThrow(() -> new DomainViolationException("Purchase Order Not Found", "Target PO was not found."));

        if (!po.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot receive items for PO belonging to another organization.");
        }

        if (po.getStatus() != PurchaseOrder.Status.ISSUED) {
            throw new DomainViolationException("Invalid Operation", "Items can only be received for ISSUED purchase orders.");
        }

        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DomainViolationException("Item Not Found", "Target PO line item was not found."));

        item.receiveQuantity(quantityReceived);
        itemRepository.save(item);

        List<Asset> createdAssets = new java.util.ArrayList<>();
        for (int i = 1; i <= quantityReceived; i++) {
            String itemIdSub = item.getId() != null ? item.getId().toString().substring(0, 4) : "ITEM";
            String tag = "AST-PO-" + po.getPoNumber() + "-" + itemIdSub + "-" + i;
            Asset asset = new Asset(
                    organizationId,
                    item.getCategoryId() != null ? item.getCategoryId() : UUID.randomUUID(),
                    tag,
                    item.getItemDescription(),
                    "Procured via PO #" + po.getPoNumber(),
                    null,
                    "Warehouse Receiving"
            );
            asset.setStatus(AssetStatus.AVAILABLE);
            createdAssets.add(assetRepository.save(asset));
        }

        List<PurchaseOrderItem> allItems = itemRepository.findByPurchaseOrderId(purchaseOrderId);
        boolean allReceived = allItems.stream().allMatch(PurchaseOrderItem::isFullyReceived);
        if (allReceived) {
            po.fulfill();
            repository.save(po);
            log.info("PO {} fully fulfilled after receiving all items", purchaseOrderId);
        }

        return createdAssets;
    }

    @Transactional
    public PurchaseOrder issuePurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new DomainViolationException("Purchase Order Not Found", "Purchase order not found: " + poId));
        po.issue();
        return repository.save(po);
    }

    @Transactional
    public PurchaseOrder fulfillPurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new DomainViolationException("Purchase Order Not Found", "Purchase order not found: " + poId));
        po.fulfill();
        return repository.save(po);
    }

    @Transactional
    public PurchaseOrder cancelPurchaseOrder(UUID poId) {
        PurchaseOrder po = repository.findById(poId)
                .orElseThrow(() -> new DomainViolationException("Purchase Order Not Found", "Purchase order not found: " + poId));
        po.cancel();
        return repository.save(po);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getPurchaseOrdersForVendor(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
    }
}
