package com.assetflow.asset.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "category_field_definitions")
public class CategoryFieldDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "field_label", nullable = false)
    private String fieldLabel;

    @Column(name = "field_type", nullable = false, length = 50)
    private String fieldType;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    @Column(name = "validation_regex")
    private String validationRegex;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "min_value")
    private BigDecimal minValue;

    @Column(name = "max_value")
    private BigDecimal maxValue;

    public CategoryFieldDefinition() {
    }

    public CategoryFieldDefinition(UUID organizationId, UUID categoryId, String fieldName, String fieldLabel, String fieldType, boolean required, String options) {
        this(organizationId, categoryId, fieldName, fieldLabel, fieldType, required, options, null, null, null, null);
    }

    public CategoryFieldDefinition(
            UUID organizationId,
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
        this.organizationId = organizationId;
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

    public void validateFieldValue(String value) {
        if (required && (value == null || value.trim().isEmpty())) {
            throw new IllegalArgumentException("Field '" + fieldLabel + "' is required");
        }

        if (value == null || value.trim().isEmpty()) {
            return;
        }

        if (validationRegex != null && !validationRegex.isBlank()) {
            if (!value.matches(validationRegex)) {
                throw new IllegalArgumentException("Value '" + value + "' does not match required pattern for " + fieldLabel);
            }
        }

        if ("NUMBER".equalsIgnoreCase(fieldType)) {
            try {
                BigDecimal num = new BigDecimal(value.trim());
                if (minValue != null && num.compareTo(minValue) < 0) {
                    throw new IllegalArgumentException("Value for " + fieldLabel + " cannot be less than " + minValue);
                }
                if (maxValue != null && num.compareTo(maxValue) > 0) {
                    throw new IllegalArgumentException("Value for " + fieldLabel + " cannot be greater than " + maxValue);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value for " + fieldLabel + " must be a valid number");
            }
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

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
