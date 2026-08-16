package com.assetflow.shared.application;

import com.assetflow.shared.domain.*;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseRequisitionUseCase {

    private static final Logger log = LoggerFactory.getLogger(PurchaseRequisitionUseCase.class);

    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionItemRepository itemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    public PurchaseRequisitionUseCase(
            PurchaseRequisitionRepository requisitionRepository,
            PurchaseRequisitionItemRepository itemRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository) {
        this.requisitionRepository = requisitionRepository;
        this.itemRepository = itemRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    public record RequisitionItemInput(
            String itemDescription,
            UUID categoryId,
            BigDecimal estimatedUnitPrice,
            int quantity
    ) {}

    @Transactional
    public PurchaseRequisition createRequisition(
            UUID organizationId,
            UUID requesterId,
            UUID departmentId,
            String requisitionNumber,
            String title,
            String justification,
            String currency,
            PurchaseRequisition.Urgency urgency,
            List<RequisitionItemInput> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (RequisitionItemInput item : items) {
                BigDecimal unitPrice = item.estimatedUnitPrice() != null ? item.estimatedUnitPrice() : BigDecimal.ZERO;
                total = total.add(unitPrice.multiply(BigDecimal.valueOf(Math.max(1, item.quantity()))));
            }
        }

        PurchaseRequisition requisition = PurchaseRequisition.create(
                organizationId,
                requesterId,
                departmentId,
                requisitionNumber,
                title,
                justification,
                total,
                currency,
                urgency
        );

        PurchaseRequisition savedRequisition = requisitionRepository.save(requisition);

        if (items != null && !items.isEmpty()) {
            for (RequisitionItemInput itemInput : items) {
                PurchaseRequisitionItem item = new PurchaseRequisitionItem(
                        organizationId,
                        savedRequisition.getId(),
                        itemInput.itemDescription(),
                        itemInput.categoryId(),
                        itemInput.estimatedUnitPrice() != null ? itemInput.estimatedUnitPrice() : BigDecimal.ZERO,
                        Math.max(1, itemInput.quantity())
                );
                itemRepository.save(item);
            }
        }

        log.info("Created purchase requisition {} ({}) for org {}", savedRequisition.getId(), requisitionNumber, organizationId);
        return savedRequisition;
    }

    @Transactional
    public PurchaseRequisition approveRequisition(UUID organizationId, UUID requisitionId, UUID approverId, String notes) {
        PurchaseRequisition req = requisitionRepository.findByOrganizationIdAndId(organizationId, requisitionId)
                .orElseThrow(() -> new DomainViolationException("Purchase requisition not found"));

        req.approve(approverId, notes);
        PurchaseRequisition saved = requisitionRepository.save(req);
        log.info("Approved purchase requisition {} by approver {}", requisitionId, approverId);
        return saved;
    }

    @Transactional
    public PurchaseRequisition rejectRequisition(UUID organizationId, UUID requisitionId, UUID approverId, String notes) {
        PurchaseRequisition req = requisitionRepository.findByOrganizationIdAndId(organizationId, requisitionId)
                .orElseThrow(() -> new DomainViolationException("Purchase requisition not found"));

        req.reject(approverId, notes);
        PurchaseRequisition saved = requisitionRepository.save(req);
        log.info("Rejected purchase requisition {} by approver {}", requisitionId, approverId);
        return saved;
    }

    @Transactional
    public PurchaseOrder convertToPurchaseOrder(
            UUID organizationId,
            UUID requisitionId,
            UUID vendorId,
            String poNumber) {
        PurchaseRequisition req = requisitionRepository.findByOrganizationIdAndId(organizationId, requisitionId)
                .orElseThrow(() -> new DomainViolationException("Purchase requisition not found"));

        if (req.getStatus() != PurchaseRequisition.RequisitionStatus.APPROVED) {
            throw new DomainViolationException("Only APPROVED requisitions can be converted to Purchase Orders");
        }

        PurchaseOrder po = PurchaseOrder.create(organizationId, vendorId, poNumber, req.getEstimatedTotalAmount());
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        List<PurchaseRequisitionItem> reqItems = itemRepository.findByRequisitionId(requisitionId);
        for (PurchaseRequisitionItem reqItem : reqItems) {
            PurchaseOrderItem poItem = new PurchaseOrderItem(
                    organizationId,
                    savedPo.getId(),
                    reqItem.getItemDescription(),
                    reqItem.getCategoryId(),
                    reqItem.getEstimatedUnitPrice(),
                    reqItem.getQuantity()
            );
            purchaseOrderItemRepository.save(poItem);
        }

        req.markConvertedToPo(savedPo.getId());
        requisitionRepository.save(req);

        log.info("Converted purchase requisition {} to purchase order {}", requisitionId, savedPo.getId());
        return savedPo;
    }

    @Transactional
    public PurchaseRequisition cancelRequisition(UUID organizationId, UUID requisitionId) {
        PurchaseRequisition req = requisitionRepository.findByOrganizationIdAndId(organizationId, requisitionId)
                .orElseThrow(() -> new DomainViolationException("Purchase requisition not found"));

        req.cancel();
        PurchaseRequisition saved = requisitionRepository.save(req);
        log.info("Cancelled purchase requisition {}", requisitionId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequisition> getRequisitions(UUID organizationId) {
        return requisitionRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseRequisition> getRequisitionById(UUID organizationId, UUID requisitionId) {
        return requisitionRepository.findByOrganizationIdAndId(organizationId, requisitionId);
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequisitionItem> getRequisitionItems(UUID organizationId, UUID requisitionId) {
        return itemRepository.findByRequisitionId(requisitionId);
    }
}
