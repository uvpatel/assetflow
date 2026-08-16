package com.assetflow.shared.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PurchaseRequisition {

    private final UUID id;
    private final UUID organizationId;
    private final String requisitionNumber;
    private final UUID requesterId;
    private UUID departmentId;
    private String title;
    private String businessJustification;
    private BigDecimal estimatedTotalAmount;
    private String currency;
    private Urgency urgency;
    private RequisitionStatus status;
    private UUID approverId;
    private String approvalNotes;
    private Instant approvedAt;
    private UUID convertedPurchaseOrderId;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum RequisitionStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        CONVERTED_TO_PO,
        CANCELLED
    }

    public enum Urgency {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL
    }

    public PurchaseRequisition(
            UUID id,
            UUID organizationId,
            String requisitionNumber,
            UUID requesterId,
            UUID departmentId,
            String title,
            String businessJustification,
            BigDecimal estimatedTotalAmount,
            String currency,
            Urgency urgency,
            RequisitionStatus status,
            UUID approverId,
            String approvalNotes,
            Instant approvedAt,
            UUID convertedPurchaseOrderId,
            Instant createdAt,
            Instant updatedAt) {
        if (requisitionNumber == null || requisitionNumber.isBlank()) {
            throw new IllegalArgumentException("Requisition number cannot be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (estimatedTotalAmount == null || estimatedTotalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estimated total amount cannot be negative");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId cannot be null");
        this.requisitionNumber = requisitionNumber;
        this.requesterId = Objects.requireNonNull(requesterId, "requesterId cannot be null");
        this.departmentId = departmentId;
        this.title = title;
        this.businessJustification = businessJustification != null ? businessJustification : "";
        this.estimatedTotalAmount = estimatedTotalAmount;
        this.currency = currency != null && !currency.isBlank() ? currency : "USD";
        this.urgency = urgency != null ? urgency : Urgency.NORMAL;
        this.status = status != null ? status : RequisitionStatus.PENDING_APPROVAL;
        this.approverId = approverId;
        this.approvalNotes = approvalNotes;
        this.approvedAt = approvedAt;
        this.convertedPurchaseOrderId = convertedPurchaseOrderId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static PurchaseRequisition create(
            UUID organizationId,
            UUID requesterId,
            UUID departmentId,
            String requisitionNumber,
            String title,
            String businessJustification,
            BigDecimal estimatedTotalAmount,
            String currency,
            Urgency urgency) {
        Instant now = Instant.now();
        return new PurchaseRequisition(
                UUID.randomUUID(),
                organizationId,
                requisitionNumber,
                requesterId,
                departmentId,
                title,
                businessJustification,
                estimatedTotalAmount != null ? estimatedTotalAmount : BigDecimal.ZERO,
                currency,
                urgency != null ? urgency : Urgency.NORMAL,
                RequisitionStatus.PENDING_APPROVAL,
                null,
                null,
                null,
                null,
                now,
                now
        );
    }

    public void approve(UUID approverId, String notes) {
        if (this.status != RequisitionStatus.PENDING_APPROVAL && this.status != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Only PENDING_APPROVAL or DRAFT requisitions can be approved");
        }
        this.status = RequisitionStatus.APPROVED;
        this.approverId = approverId;
        this.approvalNotes = notes;
        this.approvedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void reject(UUID approverId, String notes) {
        if (this.status != RequisitionStatus.PENDING_APPROVAL && this.status != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Only PENDING_APPROVAL or DRAFT requisitions can be rejected");
        }
        this.status = RequisitionStatus.REJECTED;
        this.approverId = approverId;
        this.approvalNotes = notes;
        this.updatedAt = Instant.now();
    }

    public void markConvertedToPo(UUID purchaseOrderId) {
        if (this.status != RequisitionStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED requisitions can be converted to Purchase Orders");
        }
        this.status = RequisitionStatus.CONVERTED_TO_PO;
        this.convertedPurchaseOrderId = Objects.requireNonNull(purchaseOrderId, "purchaseOrderId cannot be null");
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == RequisitionStatus.CONVERTED_TO_PO) {
            throw new IllegalStateException("Requisitions converted to PO cannot be cancelled");
        }
        this.status = RequisitionStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void updateTotalAmount(BigDecimal newTotal) {
        if (newTotal == null || newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estimated total amount cannot be negative");
        }
        this.estimatedTotalAmount = newTotal;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBusinessJustification() {
        return businessJustification;
    }

    public void setBusinessJustification(String businessJustification) {
        this.businessJustification = businessJustification;
    }

    public BigDecimal getEstimatedTotalAmount() {
        return estimatedTotalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public RequisitionStatus getStatus() {
        return status;
    }

    public void setStatus(RequisitionStatus status) {
        this.status = status;
    }

    public UUID getApproverId() {
        return approverId;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public UUID getConvertedPurchaseOrderId() {
        return convertedPurchaseOrderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
