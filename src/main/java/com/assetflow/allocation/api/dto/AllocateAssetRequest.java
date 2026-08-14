package com.assetflow.allocation.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class AllocateAssetRequest {

    @NotNull(message = "Asset ID is required")
    private UUID assetId;

    private UUID allocatedToEmployeeId;
    private UUID allocatedToDepartmentId;
    private Instant expectedReturnAt;
    private String notes;

    public AllocateAssetRequest() {
    }

    public AllocateAssetRequest(UUID assetId, UUID allocatedToEmployeeId, UUID allocatedToDepartmentId, Instant expectedReturnAt, String notes) {
        this.assetId = assetId;
        this.allocatedToEmployeeId = allocatedToEmployeeId;
        this.allocatedToDepartmentId = allocatedToDepartmentId;
        this.expectedReturnAt = expectedReturnAt;
        this.notes = notes;
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

    public Instant getExpectedReturnAt() {
        return expectedReturnAt;
    }

    public void setExpectedReturnAt(Instant expectedReturnAt) {
        this.expectedReturnAt = expectedReturnAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
