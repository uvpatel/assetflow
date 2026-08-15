package com.assetflow.asset.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AssetWarranty {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final String providerName;
    private final String policyNumber;
    private final Instant startDate;
    private final Instant endDate;
    private final String coverageDetails;
    private final Instant createdAt;
    private Instant updatedAt;

    public AssetWarranty(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String providerName,
            String policyNumber,
            Instant startDate,
            Instant endDate,
            String coverageDetails,
            Instant createdAt,
            Instant updatedAt) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Warranty end date cannot be before start date");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.providerName = providerName;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverageDetails = coverageDetails;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AssetWarranty create(
            UUID organizationId,
            UUID assetId,
            String providerName,
            String policyNumber,
            Instant startDate,
            Instant endDate,
            String coverageDetails) {
        Instant now = Instant.now();
        return new AssetWarranty(
                UUID.randomUUID(),
                organizationId,
                assetId,
                providerName,
                policyNumber,
                startDate,
                endDate,
                coverageDetails,
                now,
                now
        );
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(endDate);
    }

    public boolean isExpiringWithin(Instant now, int days) {
        if (isExpired(now)) return false;
        Instant threshold = now.plus(days, ChronoUnit.DAYS);
        return endDate.isBefore(threshold) || endDate.equals(threshold);
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public String getProviderName() { return providerName; }
    public String getPolicyNumber() { return policyNumber; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public String getCoverageDetails() { return coverageDetails; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
