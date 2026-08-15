package com.assetflow.shared.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "data_retention_execution_logs")
public class DataRetentionExecutionLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

    @Column(name = "target_module", nullable = false)
    private String targetModule;

    @Column(nullable = false)
    private String action;

    @Column(name = "cutoff_timestamp", nullable = false)
    private Instant cutoffTimestamp;

    @Column(name = "records_affected", nullable = false)
    private int recordsAffected;

    @Column(nullable = false)
    private String status = "COMPLETED";

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt = Instant.now();

    public DataRetentionExecutionLog() {
    }

    public DataRetentionExecutionLog(UUID organizationId, UUID policyId, String targetModule, String action, Instant cutoffTimestamp, int recordsAffected) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.policyId = Objects.requireNonNull(policyId, "Policy ID cannot be null");
        this.targetModule = Objects.requireNonNull(targetModule, "Target module cannot be null");
        this.action = Objects.requireNonNull(action, "Action cannot be null");
        this.cutoffTimestamp = Objects.requireNonNull(cutoffTimestamp, "Cutoff timestamp cannot be null");
        this.recordsAffected = recordsAffected;
        this.status = "COMPLETED";
        this.executedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getPolicyId() { return policyId; }
    public String getTargetModule() { return targetModule; }
    public String getAction() { return action; }
    public Instant getCutoffTimestamp() { return cutoffTimestamp; }
    public int getRecordsAffected() { return recordsAffected; }
    public String getStatus() { return status; }
    public Instant getExecutedAt() { return executedAt; }
}
