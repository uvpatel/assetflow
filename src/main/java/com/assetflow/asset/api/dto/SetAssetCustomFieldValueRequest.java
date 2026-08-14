package com.assetflow.asset.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SetAssetCustomFieldValueRequest {

    @NotNull(message = "Field definition ID is required")
    private UUID fieldDefinitionId;

    private String fieldValue;

    public SetAssetCustomFieldValueRequest() {
    }

    public SetAssetCustomFieldValueRequest(UUID fieldDefinitionId, String fieldValue) {
        this.fieldDefinitionId = fieldDefinitionId;
        this.fieldValue = fieldValue;
    }

    public UUID getFieldDefinitionId() {
        return fieldDefinitionId;
    }

    public void setFieldDefinitionId(UUID fieldDefinitionId) {
        this.fieldDefinitionId = fieldDefinitionId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
