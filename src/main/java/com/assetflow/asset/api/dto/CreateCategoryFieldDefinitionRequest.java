package com.assetflow.asset.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateCategoryFieldDefinitionRequest {

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Field name is required")
    private String fieldName;

    @NotBlank(message = "Field label is required")
    private String fieldLabel;

    @NotBlank(message = "Field type is required")
    private String fieldType;

    private boolean required;

    private String options;

    public CreateCategoryFieldDefinitionRequest() {
    }

    public CreateCategoryFieldDefinitionRequest(UUID categoryId, String fieldName, String fieldLabel, String fieldType, boolean required, String options) {
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.required = required;
        this.options = options;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
