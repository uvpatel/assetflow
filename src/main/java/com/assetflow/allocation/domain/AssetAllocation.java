package com.assetflow.allocation.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_allocations")
public class AssetAllocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "allocated_to_employee_id")
    private UUID allocatedToEmployeeId;

    @Column(name = "allocated_to_department_id")
    private UUID allocatedToDepartmentId;

    @Column(name = "allocated_at", nullable = false)
    private Instant allocatedAt = Instant.now();

    @Column(name = "expected_return_at")
    private Instant expectedReturnAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Column(name = "return_condition_state")
    private String returnConditionState;

    private String notes;

    public AssetAllocation() {
    }

    public AssetAllocation(UUID organizationId, UUID assetId, UUID allocatedToEmployeeId, UUID allocatedToDepartmentId, Instant expectedReturnAt, String notes) {
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.allocatedToEmployeeId = allocatedToEmployeeId;
        this.allocatedToDepartmentId = allocatedToDepartmentId;
        this.expectedReturnAt = expectedReturnAt;
        this.notes = notes;
        this.allocatedAt = Instant.now();
    }

    public void markReturned(String returnConditionState) {
        this.returnedAt = Instant.now();
        this.returnConditionState = returnConditionState;
    }

    public boolean isActive() {
        return this.returnedAt == null;
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

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public UUID getAllocatedToEmployeeId() {
        return allocatedToEmployeeId;
    }

    public void setAllocatedToEmployeeId(UUID allocatedToEmployeeId) {
        this.allocatedToEmployeeId = allocatedToEmployeeId;
    }

    public UUID getAllocatedToDepartmentId() {
        return allocatedToDepartmentId;
    }

    public void setAllocatedToDepartmentId(UUID allocatedToDepartmentId) {
        this.allocatedToDepartmentId = allocatedToDepartmentId;
    }

    public Instant getAllocatedAt() {
        return allocatedAt;
    }

    public void setAllocatedAt(Instant allocatedAt) {
        this.allocatedAt = allocatedAt;
    }

    public Instant getExpectedReturnAt() {
        return expectedReturnAt;
    }

    public void setExpectedReturnAt(Instant expectedReturnAt) {
        this.expectedReturnAt = expectedReturnAt;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getReturnConditionState() {
        return returnConditionState;
    }

    public void setReturnConditionState(String returnConditionState) {
        this.returnConditionState = returnConditionState;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
