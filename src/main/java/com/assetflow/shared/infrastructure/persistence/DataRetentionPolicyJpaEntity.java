package com.assetflow.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "data_retention_policies")
public class DataRetentionPolicyJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "target_module", nullable = false)
    private String targetModule;

    @Column(name = "retention_days", nullable = false)
    private int retentionDays;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "last_executed_at")
    private Instant lastExecutedAt;

    @Column(name = "records_processed")
    private int recordsProcessed;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DataRetentionPolicyJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public String getTargetModule() { return targetModule; }
    public void setTargetModule(String targetModule) { this.targetModule = targetModule; }

    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Instant getLastExecutedAt() { return lastExecutedAt; }
    public void setLastExecutedAt(Instant lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; }

    public int getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
