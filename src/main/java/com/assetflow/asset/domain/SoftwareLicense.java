package com.assetflow.asset.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class SoftwareLicense {
    private final UUID id;
    private final UUID organizationId;
    private final String softwareName;
    private final String licenseKey;
    private final int totalSeats;
    private int allocatedSeats;
    private final LocalDate expirationDate;
    private final Instant createdAt;
    private Instant updatedAt;

    public SoftwareLicense(
            UUID id,
            UUID organizationId,
            String softwareName,
            String licenseKey,
            int totalSeats,
            int allocatedSeats,
            LocalDate expirationDate,
            Instant createdAt,
            Instant updatedAt) {
        if (softwareName == null || softwareName.isBlank()) {
            throw new IllegalArgumentException("Software name cannot be blank");
        }
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        if (allocatedSeats < 0 || allocatedSeats > totalSeats) {
            throw new IllegalArgumentException("Allocated seats cannot exceed total seats or be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.softwareName = softwareName;
        this.licenseKey = licenseKey;
        this.totalSeats = totalSeats;
        this.allocatedSeats = allocatedSeats;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SoftwareLicense create(
            UUID organizationId,
            String softwareName,
            String licenseKey,
            int totalSeats,
            LocalDate expirationDate) {
        Instant now = Instant.now();
        return new SoftwareLicense(
                UUID.randomUUID(),
                organizationId,
                softwareName,
                licenseKey,
                totalSeats,
                0,
                expirationDate,
                now,
                now
        );
    }

    public void allocateSeat() {
        if (allocatedSeats >= totalSeats) {
            throw new IllegalStateException("All software license seats are currently allocated");
        }
        this.allocatedSeats++;
        this.updatedAt = Instant.now();
    }

    public void revokeSeat() {
        if (allocatedSeats <= 0) {
            throw new IllegalStateException("No allocated seats available to revoke");
        }
        this.allocatedSeats--;
        this.updatedAt = Instant.now();
    }

    public int getAvailableSeats() {
        return totalSeats - allocatedSeats;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getSoftwareName() { return softwareName; }
    public String getLicenseKey() { return licenseKey; }
    public int getTotalSeats() { return totalSeats; }
    public int getAllocatedSeats() { return allocatedSeats; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
