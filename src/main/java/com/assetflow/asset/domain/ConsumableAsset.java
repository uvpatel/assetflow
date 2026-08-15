package com.assetflow.asset.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ConsumableAsset {
    private final UUID id;
    private final UUID organizationId;
    private final String name;
    private final String sku;
    private final UUID categoryId;
    private int quantityOnHand;
    private final int minimumThreshold;
    private final String unitOfMeasure;
    private final BigDecimal unitCost;
    private final Instant createdAt;
    private Instant updatedAt;

    public ConsumableAsset(
            UUID id,
            UUID organizationId,
            String name,
            String sku,
            UUID categoryId,
            int quantityOnHand,
            int minimumThreshold,
            String unitOfMeasure,
            BigDecimal unitCost,
            Instant createdAt,
            Instant updatedAt) {
        if (quantityOnHand < 0) {
            throw new IllegalArgumentException("Quantity on hand cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.sku = sku;
        this.categoryId = categoryId;
        this.quantityOnHand = quantityOnHand;
        this.minimumThreshold = minimumThreshold;
        this.unitOfMeasure = unitOfMeasure;
        this.unitCost = unitCost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ConsumableAsset create(
            UUID organizationId,
            String name,
            String sku,
            UUID categoryId,
            int initialQuantity,
            int minimumThreshold,
            String unitOfMeasure,
            BigDecimal unitCost) {
        Instant now = Instant.now();
        return new ConsumableAsset(
                UUID.randomUUID(),
                organizationId,
                name,
                sku,
                categoryId,
                initialQuantity,
                minimumThreshold,
                unitOfMeasure,
                unitCost,
                now,
                now
        );
    }

    public void restock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be positive");
        }
        this.quantityOnHand += amount;
        this.updatedAt = Instant.now();
    }

    public void disburse(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Disburse amount must be positive");
        }
        if (this.quantityOnHand < amount) {
            throw new IllegalStateException("Insufficient stock available");
        }
        this.quantityOnHand -= amount;
        this.updatedAt = Instant.now();
    }

    public boolean isBelowThreshold() {
        return quantityOnHand <= minimumThreshold;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public UUID getCategoryId() { return categoryId; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public int getMinimumThreshold() { return minimumThreshold; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public BigDecimal getUnitCost() { return unitCost; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
