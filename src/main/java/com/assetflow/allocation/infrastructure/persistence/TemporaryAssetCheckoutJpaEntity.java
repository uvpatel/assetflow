package com.assetflow.allocation.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "temporary_asset_checkouts")
public class TemporaryAssetCheckoutJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "borrower_employee_id", nullable = false)
    private UUID borrowerEmployeeId;

    @Column(name = "checkout_time", nullable = false)
    private Instant checkoutTime;

    @Column(name = "expected_return_time", nullable = false)
    private Instant expectedReturnTime;

    @Column(name = "actual_return_time")
    private Instant actualReturnTime;

    @Column(name = "checkout_condition_notes")
    private String checkoutConditionNotes;

    @Column(name = "return_condition_notes")
    private String returnConditionNotes;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TemporaryAssetCheckoutJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }

    public UUID getBorrowerEmployeeId() { return borrowerEmployeeId; }
    public void setBorrowerEmployeeId(UUID borrowerEmployeeId) { this.borrowerEmployeeId = borrowerEmployeeId; }

    public Instant getCheckoutTime() { return checkoutTime; }
    public void setCheckoutTime(Instant checkoutTime) { this.checkoutTime = checkoutTime; }

    public Instant getExpectedReturnTime() { return expectedReturnTime; }
    public void setExpectedReturnTime(Instant expectedReturnTime) { this.expectedReturnTime = expectedReturnTime; }

    public Instant getActualReturnTime() { return actualReturnTime; }
    public void setActualReturnTime(Instant actualReturnTime) { this.actualReturnTime = actualReturnTime; }

    public String getCheckoutConditionNotes() { return checkoutConditionNotes; }
    public void setCheckoutConditionNotes(String checkoutConditionNotes) { this.checkoutConditionNotes = checkoutConditionNotes; }

    public String getReturnConditionNotes() { return returnConditionNotes; }
    public void setReturnConditionNotes(String returnConditionNotes) { this.returnConditionNotes = returnConditionNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
