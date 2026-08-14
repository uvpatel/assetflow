package com.assetflow.allocation.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer_requests")
public class TransferRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "from_department_id")
    private UUID fromDepartmentId;

    @Column(name = "to_department_id", nullable = false)
    private UUID toDepartmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferRequestStatus status = TransferRequestStatus.PENDING;

    private String reason;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    public TransferRequest() {
    }

    public TransferRequest(UUID organizationId, UUID employeeId, UUID fromDepartmentId, UUID toDepartmentId, String reason) {
        this.organizationId = organizationId;
        this.employeeId = employeeId;
        this.fromDepartmentId = fromDepartmentId;
        this.toDepartmentId = toDepartmentId;
        this.reason = reason;
        this.status = TransferRequestStatus.PENDING;
    }

    public void approve(String reviewer) {
        this.status = TransferRequestStatus.APPROVED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = reviewer;
    }

    public void reject(String reviewer, String rejectionReason) {
        this.status = TransferRequestStatus.REJECTED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = reviewer;
        this.rejectionReason = rejectionReason;
    }

    public boolean isPending() {
        return this.status == TransferRequestStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getFromDepartmentId() {
        return fromDepartmentId;
    }

    public void setFromDepartmentId(UUID fromDepartmentId) {
        this.fromDepartmentId = fromDepartmentId;
    }

    public UUID getToDepartmentId() {
        return toDepartmentId;
    }

    public void setToDepartmentId(UUID toDepartmentId) {
        this.toDepartmentId = toDepartmentId;
    }

    public TransferRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TransferRequestStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
