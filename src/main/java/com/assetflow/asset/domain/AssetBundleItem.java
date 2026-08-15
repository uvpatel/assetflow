package com.assetflow.asset.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AssetBundleItem {
    private final UUID id;
    private final UUID organizationId;
    private final UUID parentAssetId;
    private final UUID childAssetId;
    private final RelationshipType relationshipType;
    private final int quantity;
    private final String notes;
    private final Instant createdAt;

    public enum RelationshipType {
        ACCESSORY,
        COMPONENT,
        BUNDLE_MEMBER
    }

    public AssetBundleItem(
            UUID id,
            UUID organizationId,
            UUID parentAssetId,
            UUID childAssetId,
            RelationshipType relationshipType,
            int quantity,
            String notes,
            Instant createdAt) {
        if (Objects.equals(parentAssetId, childAssetId)) {
            throw new IllegalArgumentException("An asset cannot be bundled with itself");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.parentAssetId = parentAssetId;
        this.childAssetId = childAssetId;
        this.relationshipType = relationshipType;
        this.quantity = quantity;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static AssetBundleItem create(
            UUID organizationId,
            UUID parentAssetId,
            UUID childAssetId,
            RelationshipType type,
            int quantity,
            String notes) {
        return new AssetBundleItem(
                UUID.randomUUID(),
                organizationId,
                parentAssetId,
                childAssetId,
                type,
                quantity,
                notes,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getParentAssetId() { return parentAssetId; }
    public UUID getChildAssetId() { return childAssetId; }
    public RelationshipType getRelationshipType() { return relationshipType; }
    public int getQuantity() { return quantity; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
}
