package com.assetflow.allocation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "custody_transfer_items")
public class CustodyTransferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "custody_transfer_id", nullable = false)
    private UUID custodyTransferId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "item_condition")
    private String itemCondition;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public CustodyTransferItem() {
    }

    public CustodyTransferItem(UUID custodyTransferId, UUID assetId, String itemCondition, String notes) {
        this.custodyTransferId = Objects.requireNonNull(custodyTransferId, "Custody transfer ID cannot be null");
        this.assetId = Objects.requireNonNull(assetId, "Asset ID cannot be null");
        this.itemCondition = itemCondition != null ? itemCondition : "GOOD";
        this.notes = notes;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustodyTransferId() {
        return custodyTransferId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
