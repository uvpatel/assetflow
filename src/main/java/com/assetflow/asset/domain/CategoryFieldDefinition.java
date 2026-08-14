package com.assetflow.asset.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

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

    public CategoryFieldDefinition() {
    }

    public CategoryFieldDefinition(UUID organizationId, UUID categoryId, String fieldName, String fieldLabel, String fieldType, boolean required, String options) {
        this.organizationId = organizationId;
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.required = required;
        this.options = options;
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
