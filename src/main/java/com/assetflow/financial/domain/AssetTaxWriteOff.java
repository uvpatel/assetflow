package com.assetflow.financial.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AssetTaxWriteOff {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final int fiscalYear;
    private final BigDecimal writeoffAmount;
    private final BigDecimal remainingTaxBasis;
    private final String writeoffReason;
    private final Instant createdAt;

    public AssetTaxWriteOff(
            UUID id,
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal writeoffAmount,
            BigDecimal remainingTaxBasis,
            String writeoffReason,
            Instant createdAt) {
        if (fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("Fiscal year must be a valid 4-digit year");
        }
        if (writeoffAmount == null || writeoffAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Write-off amount must be greater than zero");
        }
        if (remainingTaxBasis == null || remainingTaxBasis.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Remaining tax basis cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.fiscalYear = fiscalYear;
        this.writeoffAmount = writeoffAmount;
        this.remainingTaxBasis = remainingTaxBasis;
        this.writeoffReason = writeoffReason;
        this.createdAt = createdAt;
    }

    public static AssetTaxWriteOff executeWriteOff(
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal currentTaxBasis,
            BigDecimal writeoffAmount,
            String writeoffReason) {
        if (currentTaxBasis == null || writeoffAmount.compareTo(currentTaxBasis) > 0) {
            throw new IllegalArgumentException("Write-off amount cannot exceed current tax basis");
        }
        BigDecimal newRemainingBasis = currentTaxBasis.subtract(writeoffAmount);

        return new AssetTaxWriteOff(
                UUID.randomUUID(),
                organizationId,
                assetId,
                fiscalYear,
                writeoffAmount,
                newRemainingBasis,
                writeoffReason,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public int getFiscalYear() { return fiscalYear; }
    public BigDecimal getWriteoffAmount() { return writeoffAmount; }
    public BigDecimal getRemainingTaxBasis() { return remainingTaxBasis; }
    public String getWriteoffReason() { return writeoffReason; }
    public Instant getCreatedAt() { return createdAt; }
}
