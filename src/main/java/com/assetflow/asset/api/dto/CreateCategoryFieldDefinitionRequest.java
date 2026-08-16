package com.assetflow.asset.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
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

    private String validationRegex;

    private String defaultValue;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    public CreateCategoryFieldDefinitionRequest() {
    }

    public CreateCategoryFieldDefinitionRequest(UUID categoryId, String fieldName, String fieldLabel, String fieldType, boolean required, String options) {
        this(categoryId, fieldName, fieldLabel, fieldType, required, options, null, null, null, null);
    }

    public CreateCategoryFieldDefinitionRequest(
            UUID categoryId,
            String fieldName,
            String fieldLabel,
            String fieldType,
            boolean required,
            String options,
            String validationRegex,
            String defaultValue,
            BigDecimal minValue,
            BigDecimal maxValue) {
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.required = required;
        this.options = options;
        this.validationRegex = validationRegex;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLabel() { return fieldLabel; }
    public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getValidationRegex() { return validationRegex; }
    public void setValidationRegex(String validationRegex) { this.validationRegex = validationRegex; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public BigDecimal getMinValue() { return minValue; }
    public void setMinValue(BigDecimal minValue) { this.minValue = minValue; }

    public BigDecimal getMaxValue() { return maxValue; }
    public void setMaxValue(BigDecimal maxValue) { this.maxValue = maxValue; }
}
