package com.assetflow.maintenance.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AssetMaintenanceSchedule {

    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final String title;
    private final int frequencyDays;
    private Instant nextDueDate;
    private Instant lastPerformedAt;
    private ScheduleStatus status;
    private boolean autoGenerateWorkOrder;
    private String priority;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum ScheduleStatus {
        ACTIVE,
        PAUSED,
        OVERDUE
    }

    public AssetMaintenanceSchedule(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant nextDueDate,
            Instant lastPerformedAt,
            ScheduleStatus status,
            boolean autoGenerateWorkOrder,
            String priority,
            Instant createdAt,
            Instant updatedAt) {
        if (frequencyDays <= 0) {
            throw new IllegalArgumentException("Frequency days must be greater than zero");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.title = title;
        this.frequencyDays = frequencyDays;
        this.nextDueDate = nextDueDate;
        this.lastPerformedAt = lastPerformedAt;
        this.status = status != null ? status : ScheduleStatus.ACTIVE;
        this.autoGenerateWorkOrder = autoGenerateWorkOrder;
        this.priority = priority != null ? priority : "MEDIUM";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AssetMaintenanceSchedule(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant nextDueDate,
            Instant lastPerformedAt,
            ScheduleStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this(id, organizationId, assetId, title, frequencyDays, nextDueDate, lastPerformedAt, status, true, "MEDIUM", createdAt, updatedAt);
    }

    public static AssetMaintenanceSchedule create(
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate,
            boolean autoGenerateWorkOrder,
            String priority) {
        Instant now = Instant.now();
        return new AssetMaintenanceSchedule(
                UUID.randomUUID(),
                organizationId,
                assetId,
                title,
                frequencyDays,
                firstDueDate,
                null,
                ScheduleStatus.ACTIVE,
                autoGenerateWorkOrder,
                priority,
                now,
                now
        );
    }

    public static AssetMaintenanceSchedule create(
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate) {
        return create(organizationId, assetId, title, frequencyDays, firstDueDate, true, "MEDIUM");
    }

    public void markPerformed(Instant performedAt) {
        this.lastPerformedAt = performedAt;
        this.nextDueDate = performedAt.plus(frequencyDays, ChronoUnit.DAYS);
        this.status = ScheduleStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void pause() {
        this.status = ScheduleStatus.PAUSED;
        this.updatedAt = Instant.now();
    }

    public void resume() {
        this.status = ScheduleStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void checkOverdue(Instant now) {
        if (status == ScheduleStatus.ACTIVE && now.isAfter(nextDueDate)) {
            this.status = ScheduleStatus.OVERDUE;
            this.updatedAt = now;
        }
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public String getTitle() { return title; }
    public int getFrequencyDays() { return frequencyDays; }
    public Instant getNextDueDate() { return nextDueDate; }
    public Instant getLastPerformedAt() { return lastPerformedAt; }
    public ScheduleStatus getStatus() { return status; }
    public boolean isAutoGenerateWorkOrder() { return autoGenerateWorkOrder; }
    public String getPriority() { return priority; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
