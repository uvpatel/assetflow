package com.assetflow.asset.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "asset_custom_field_values")
public class AssetCustomFieldValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "field_definition_id", nullable = false)
    private UUID fieldDefinitionId;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    public AssetCustomFieldValue() {
    }

    public AssetCustomFieldValue(UUID organizationId, UUID assetId, UUID fieldDefinitionId, String fieldValue) {
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.fieldDefinitionId = fieldDefinitionId;
        this.fieldValue = fieldValue;
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
