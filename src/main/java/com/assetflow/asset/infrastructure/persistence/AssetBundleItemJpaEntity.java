package com.assetflow.asset.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_bundle_items")
public class AssetBundleItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "parent_asset_id", nullable = false)
    private UUID parentAssetId;

    @Column(name = "child_asset_id", nullable = false)
    private UUID childAssetId;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AssetBundleItemJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getParentAssetId() { return parentAssetId; }
    public void setParentAssetId(UUID parentAssetId) { this.parentAssetId = parentAssetId; }

    public UUID getChildAssetId() { return childAssetId; }
    public void setChildAssetId(UUID childAssetId) { this.childAssetId = childAssetId; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
