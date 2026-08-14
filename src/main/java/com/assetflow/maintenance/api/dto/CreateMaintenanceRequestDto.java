package com.assetflow.maintenance.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateMaintenanceRequestDto {

    @NotNull(message = "Asset ID is required")
    private UUID assetId;

    @NotNull(message = "Requested by employee ID is required")
    private UUID requestedByEmployeeId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String priority = "MEDIUM";

    public CreateMaintenanceRequestDto() {
    }

    public CreateMaintenanceRequestDto(UUID assetId, UUID requestedByEmployeeId, String title, String description, String priority) {
        this.assetId = assetId;
        this.requestedByEmployeeId = requestedByEmployeeId;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public UUID getRequestedByEmployeeId() {
        return requestedByEmployeeId;
    }

    public void setRequestedByEmployeeId(UUID requestedByEmployeeId) {
        this.requestedByEmployeeId = requestedByEmployeeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
