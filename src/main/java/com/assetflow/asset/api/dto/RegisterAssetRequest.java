package com.assetflow.asset.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class RegisterAssetRequest {

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Asset tag is required")
    private String assetTag;

    @NotBlank(message = "Asset name is required")
    private String name;

    private String description;
    private String serialNumber;
    private String location;

    public RegisterAssetRequest() {
    }

    public RegisterAssetRequest(UUID categoryId, String assetTag, String name, String description, String serialNumber, String location) {
        this.categoryId = categoryId;
        this.assetTag = assetTag;
        this.name = name;
        this.description = description;
        this.serialNumber = serialNumber;
        this.location = location;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
