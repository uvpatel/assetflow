package com.assetflow.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_requisitions")
public class PurchaseRequisitionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "requisition_number", nullable = false)
    private String requisitionNumber;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(nullable = false)
    private String title;

    @Column(name = "business_justification", nullable = false, length = 1000)
    private String businessJustification;

    @Column(name = "estimated_total_amount", nullable = false)
    private BigDecimal estimatedTotalAmount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Column(nullable = false, length = 32)
    private String urgency;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "approver_id")
    private UUID approverId;

    @Column(name = "approval_notes", length = 1000)
    private String approvalNotes;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "converted_purchase_order_id")
    private UUID convertedPurchaseOrderId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PurchaseRequisitionJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public String getRequisitionNumber() { return requisitionNumber; }
    public void setRequisitionNumber(String requisitionNumber) { this.requisitionNumber = requisitionNumber; }

    public UUID getRequesterId() { return requesterId; }
    public void setRequesterId(UUID requesterId) { this.requesterId = requesterId; }

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBusinessJustification() { return businessJustification; }
    public void setBusinessJustification(String businessJustification) { this.businessJustification = businessJustification; }

    public BigDecimal getEstimatedTotalAmount() { return estimatedTotalAmount; }
    public void setEstimatedTotalAmount(BigDecimal estimatedTotalAmount) { this.estimatedTotalAmount = estimatedTotalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getApproverId() { return approverId; }
    public void setApproverId(UUID approverId) { this.approverId = approverId; }

    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }

    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

    public UUID getConvertedPurchaseOrderId() { return convertedPurchaseOrderId; }
    public void setConvertedPurchaseOrderId(UUID convertedPurchaseOrderId) { this.convertedPurchaseOrderId = convertedPurchaseOrderId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
