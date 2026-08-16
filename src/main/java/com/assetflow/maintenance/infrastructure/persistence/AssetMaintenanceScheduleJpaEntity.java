package com.assetflow.maintenance.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_maintenance_schedules")
public class AssetMaintenanceScheduleJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "frequency_days", nullable = false)
    private int frequencyDays;

    @Column(name = "next_due_date", nullable = false)
    private Instant nextDueDate;

    @Column(name = "last_performed_at")
    private Instant lastPerformedAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "auto_generate_work_order")
    private boolean autoGenerateWorkOrder = true;

    @Column(name = "priority")
    private String priority = "MEDIUM";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AssetMaintenanceScheduleJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getFrequencyDays() { return frequencyDays; }
    public void setFrequencyDays(int frequencyDays) { this.frequencyDays = frequencyDays; }

    public Instant getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(Instant nextDueDate) { this.nextDueDate = nextDueDate; }

    public Instant getLastPerformedAt() { return lastPerformedAt; }
    public void setLastPerformedAt(Instant lastPerformedAt) { this.lastPerformedAt = lastPerformedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAutoGenerateWorkOrder() { return autoGenerateWorkOrder; }
    public void setAutoGenerateWorkOrder(boolean autoGenerateWorkOrder) { this.autoGenerateWorkOrder = autoGenerateWorkOrder; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
