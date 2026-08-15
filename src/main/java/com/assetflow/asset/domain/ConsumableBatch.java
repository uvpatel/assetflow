package com.assetflow.asset.domain;

import java.time.Instant;
import java.util.UUID;

public class ConsumableBatch {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final String batchNumber;
    private int initialQuantity;
    private int currentQuantity;
    private final Instant expiryDate;
    private final Instant createdAt;

    public ConsumableBatch(UUID id, UUID organizationId, UUID assetId, String batchNumber, int initialQuantity, int currentQuantity, Instant expiryDate, Instant createdAt) {
        if (batchNumber == null || batchNumber.isBlank()) {
            throw new IllegalArgumentException("Batch number cannot be blank");
        }
        if (initialQuantity <= 0) {
            throw new IllegalArgumentException("Initial quantity must be greater than zero");
        }
        if (currentQuantity < 0 || currentQuantity > initialQuantity) {
            throw new IllegalArgumentException("Invalid current quantity");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.batchNumber = batchNumber;
        this.initialQuantity = initialQuantity;
        this.currentQuantity = currentQuantity;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public static ConsumableBatch create(UUID organizationId, UUID assetId, String batchNumber, int quantity, Instant expiryDate) {
        Instant now = Instant.now();
        return new ConsumableBatch(
                UUID.randomUUID(),
                organizationId,
                assetId,
                batchNumber,
                quantity,
                quantity,
                expiryDate,
                now
        );
    }

    public void consume(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Consumption quantity must be positive");
        }
        if (quantity > currentQuantity) {
            throw new IllegalStateException("Insufficient quantity remaining in batch");
        }
        this.currentQuantity -= quantity;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public String getBatchNumber() { return batchNumber; }
    public int getInitialQuantity() { return initialQuantity; }
    public int getCurrentQuantity() { return currentQuantity; }
    public Instant getExpiryDate() { return expiryDate; }
    public Instant getCreatedAt() { return createdAt; }
}
