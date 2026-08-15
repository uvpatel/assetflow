package com.assetflow.maintenance.domain;

import java.time.Instant;
import java.util.UUID;

public class TechnicianWorkload {
    private final UUID id;
    private final UUID organizationId;
    private final UUID technicianEmployeeId;
    private int activeTaskCount;
    private int maxTaskCapacity;
    private String specialization;
    private final Instant createdAt;
    private Instant updatedAt;

    public TechnicianWorkload(
            UUID id,
            UUID organizationId,
            UUID technicianEmployeeId,
            int activeTaskCount,
            int maxTaskCapacity,
            String specialization,
            Instant createdAt,
            Instant updatedAt) {
        if (activeTaskCount < 0) {
            throw new IllegalArgumentException("Active task count cannot be negative");
        }
        if (maxTaskCapacity <= 0) {
            throw new IllegalArgumentException("Max task capacity must be greater than zero");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.technicianEmployeeId = technicianEmployeeId;
        this.activeTaskCount = activeTaskCount;
        this.maxTaskCapacity = maxTaskCapacity;
        this.specialization = specialization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TechnicianWorkload register(
            UUID organizationId,
            UUID technicianEmployeeId,
            int maxTaskCapacity,
            String specialization) {
        Instant now = Instant.now();
        return new TechnicianWorkload(
                UUID.randomUUID(),
                organizationId,
                technicianEmployeeId,
                0,
                maxTaskCapacity,
                specialization,
                now,
                now
        );
    }

    public void assignTask() {
        if (activeTaskCount >= maxTaskCapacity) {
            throw new IllegalStateException("Technician has reached maximum task capacity");
        }
        this.activeTaskCount++;
        this.updatedAt = Instant.now();
    }

    public void completeTask() {
        if (activeTaskCount <= 0) {
            throw new IllegalStateException("No active tasks to complete");
        }
        this.activeTaskCount--;
        this.updatedAt = Instant.now();
    }

    public boolean isAvailable() {
        return activeTaskCount < maxTaskCapacity;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getTechnicianEmployeeId() { return technicianEmployeeId; }
    public int getActiveTaskCount() { return activeTaskCount; }
    public int getMaxTaskCapacity() { return maxTaskCapacity; }
    public String getSpecialization() { return specialization; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
