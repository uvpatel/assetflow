package com.assetflow.shared.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class DataRetentionPolicy {
    private final UUID id;
    private final UUID organizationId;
    private final String targetModule;
    private int retentionDays;
    private RetentionAction action;
    private boolean isActive;
    private Instant lastExecutedAt;
    private int recordsProcessed;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum RetentionAction {
        ARCHIVE,
        PURGE,
        ANONYMIZE
    }

    public DataRetentionPolicy(
            UUID id,
            UUID organizationId,
            String targetModule,
            int retentionDays,
            RetentionAction action,
            boolean isActive,
            Instant lastExecutedAt,
            int recordsProcessed,
            Instant createdAt,
            Instant updatedAt) {
        if (retentionDays <= 0) {
            throw new IllegalArgumentException("Retention days must be greater than zero");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.targetModule = targetModule;
        this.retentionDays = retentionDays;
        this.action = action;
        this.isActive = isActive;
        this.lastExecutedAt = lastExecutedAt;
        this.recordsProcessed = recordsProcessed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DataRetentionPolicy(
            UUID id,
            UUID organizationId,
            String targetModule,
            int retentionDays,
            RetentionAction action,
            boolean isActive,
            Instant createdAt,
            Instant updatedAt) {
        this(id, organizationId, targetModule, retentionDays, action, isActive, null, 0, createdAt, updatedAt);
    }

    public static DataRetentionPolicy create(
            UUID organizationId,
            String targetModule,
            int retentionDays,
            RetentionAction action) {
        Instant now = Instant.now();
        return new DataRetentionPolicy(
                UUID.randomUUID(),
                organizationId,
                targetModule,
                retentionDays,
                action,
                true,
                null,
                0,
                now,
                now
        );
    }

    public Instant calculateCutoffTimestamp(Instant referenceTime) {
        return referenceTime.minus(retentionDays, ChronoUnit.DAYS);
    }

    public void update(int retentionDays, RetentionAction action, boolean isActive) {
        if (retentionDays <= 0) {
            throw new IllegalArgumentException("Retention days must be greater than zero");
        }
        this.retentionDays = retentionDays;
        this.action = action;
        this.isActive = isActive;
        this.updatedAt = Instant.now();
    }

    public void markExecuted(int processedCount) {
        this.recordsProcessed += processedCount;
        this.lastExecutedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getTargetModule() { return targetModule; }
    public int getRetentionDays() { return retentionDays; }
    public RetentionAction getAction() { return action; }
    public boolean isActive() { return isActive; }
    public Instant getLastExecutedAt() { return lastExecutedAt; }
    public int getRecordsProcessed() { return recordsProcessed; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
