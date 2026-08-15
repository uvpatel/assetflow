package com.assetflow.asset.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        DECLINING_BALANCE,
        DOUBLE_DECLINING_BALANCE,
        SUM_OF_YEARS_DIGITS
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

    public void calculate(int elapsedMonths) {
        if (elapsedMonths <= 0 || usefulLifeMonths <= 0) return;

        switch (depreciationMethod) {
            case STRAIGHT_LINE -> calculateStraightLine(elapsedMonths);
            case DOUBLE_DECLINING_BALANCE, DECLINING_BALANCE -> calculateDoubleDecliningBalance(elapsedMonths);
            case SUM_OF_YEARS_DIGITS -> calculateSumOfYearsDigits(elapsedMonths);
        }
    }

    public void calculateStraightLine(int elapsedMonths) {
        if (elapsedMonths <= 0 || usefulLifeMonths <= 0) return;
        BigDecimal depreciableBase = initialCost.subtract(salvageValue);
        if (depreciableBase.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal monthlyDepreciation = depreciableBase.divide(BigDecimal.valueOf(usefulLifeMonths), 4, RoundingMode.HALF_UP);
        BigDecimal totalDepreciation = monthlyDepreciation.multiply(BigDecimal.valueOf(elapsedMonths))
                .setScale(2, RoundingMode.HALF_UP)
                .min(depreciableBase);

        this.accumulatedDepreciation = totalDepreciation;
        this.currentBookValue = initialCost.subtract(totalDepreciation).max(salvageValue);
        this.lastCalculatedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void calculateDoubleDecliningBalance(int elapsedMonths) {
        if (elapsedMonths <= 0 || usefulLifeMonths <= 0) return;
        BigDecimal depreciableBase = initialCost.subtract(salvageValue);
        if (depreciableBase.compareTo(BigDecimal.ZERO) <= 0) return;

        double annualRate = 2.0 / (usefulLifeMonths / 12.0);
        double monthlyRate = annualRate / 12.0;

        BigDecimal bookValue = initialCost;
        BigDecimal totalDepreciation = BigDecimal.ZERO;

        for (int m = 1; m <= Math.min(elapsedMonths, usefulLifeMonths); m++) {
            BigDecimal monthlyDep = bookValue.multiply(BigDecimal.valueOf(monthlyRate))
                    .setScale(2, RoundingMode.HALF_UP);

            if (bookValue.subtract(monthlyDep).compareTo(salvageValue) < 0) {
                monthlyDep = bookValue.subtract(salvageValue).max(BigDecimal.ZERO);
            }

            bookValue = bookValue.subtract(monthlyDep);
            totalDepreciation = totalDepreciation.add(monthlyDep);

            if (bookValue.compareTo(salvageValue) <= 0) {
                bookValue = salvageValue;
                break;
            }
        }

        this.accumulatedDepreciation = totalDepreciation.min(depreciableBase);
        this.currentBookValue = initialCost.subtract(this.accumulatedDepreciation).max(salvageValue);
        this.lastCalculatedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void calculateSumOfYearsDigits(int elapsedMonths) {
        if (elapsedMonths <= 0 || usefulLifeMonths <= 0) return;
        BigDecimal depreciableBase = initialCost.subtract(salvageValue);
        if (depreciableBase.compareTo(BigDecimal.ZERO) <= 0) return;

        int years = (int) Math.ceil(usefulLifeMonths / 12.0);
        int sumOfYears = years * (years + 1) / 2;

        BigDecimal totalDepreciation = BigDecimal.ZERO;
        int maxMonths = Math.min(elapsedMonths, usefulLifeMonths);

        for (int m = 1; m <= maxMonths; m++) {
            int currentYear = (m - 1) / 12 + 1;
            int remainingYears = years - currentYear + 1;
            double annualFraction = (double) remainingYears / sumOfYears;
            double monthlyFraction = annualFraction / 12.0;

            BigDecimal monthlyDep = depreciableBase.multiply(BigDecimal.valueOf(monthlyFraction))
                    .setScale(2, RoundingMode.HALF_UP);

            totalDepreciation = totalDepreciation.add(monthlyDep);
        }

        this.accumulatedDepreciation = totalDepreciation.min(depreciableBase);
        this.currentBookValue = initialCost.subtract(this.accumulatedDepreciation).max(salvageValue);
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
