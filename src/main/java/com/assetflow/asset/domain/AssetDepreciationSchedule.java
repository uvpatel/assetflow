package com.assetflow.asset.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AssetDepreciationSchedule {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final DepreciationMethod depreciationMethod;
    private final BigDecimal initialCost;
    private final BigDecimal salvageValue;
    private final int usefulLifeMonths;
    private BigDecimal currentBookValue;
    private BigDecimal accumulatedDepreciation;
    private Instant lastCalculatedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum DepreciationMethod {
        STRAIGHT_LINE,
        DECLINING_BALANCE
    }

    public AssetDepreciationSchedule(
            UUID id,
            UUID organizationId,
            UUID assetId,
            DepreciationMethod depreciationMethod,
            BigDecimal initialCost,
            BigDecimal salvageValue,
            int usefulLifeMonths,
            BigDecimal currentBookValue,
            BigDecimal accumulatedDepreciation,
            Instant lastCalculatedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.depreciationMethod = depreciationMethod;
        this.initialCost = initialCost;
        this.salvageValue = salvageValue;
        this.usefulLifeMonths = usefulLifeMonths;
        this.currentBookValue = currentBookValue;
        this.accumulatedDepreciation = accumulatedDepreciation;
        this.lastCalculatedAt = lastCalculatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AssetDepreciationSchedule create(
            UUID organizationId,
            UUID assetId,
            DepreciationMethod method,
            BigDecimal initialCost,
            BigDecimal salvageValue,
            int usefulLifeMonths) {
        Instant now = Instant.now();
        return new AssetDepreciationSchedule(
                UUID.randomUUID(),
                organizationId,
                assetId,
                method,
                initialCost,
                salvageValue,
                usefulLifeMonths,
                initialCost,
                BigDecimal.ZERO,
                null,
                now,
                now
        );
    }

    public void calculateStraightLine(int elapsedMonths) {
        if (elapsedMonths <= 0 || usefulLifeMonths <= 0) return;
        BigDecimal depreciableBase = initialCost.subtract(salvageValue);
        if (depreciableBase.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal monthlyDepreciation = depreciableBase.divide(BigDecimal.valueOf(usefulLifeMonths), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalDepreciation = monthlyDepreciation.multiply(BigDecimal.valueOf(elapsedMonths)).min(depreciableBase);

        this.accumulatedDepreciation = totalDepreciation;
        this.currentBookValue = initialCost.subtract(totalDepreciation);
        this.lastCalculatedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public DepreciationMethod getDepreciationMethod() { return depreciationMethod; }
    public BigDecimal getInitialCost() { return initialCost; }
    public BigDecimal getSalvageValue() { return salvageValue; }
    public int getUsefulLifeMonths() { return usefulLifeMonths; }
    public BigDecimal getCurrentBookValue() { return currentBookValue; }
    public BigDecimal getAccumulatedDepreciation() { return accumulatedDepreciation; }
    public Instant getLastCalculatedAt() { return lastCalculatedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
