package com.assetflow.audit.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_discrepancy_resolutions")
public class AuditDiscrepancyResolutionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "audit_cycle_id", nullable = false)
    private UUID auditCycleId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "discrepancy_type", nullable = false)
    private String discrepancyType;

    @Column(name = "resolution_action", nullable = false)
    private String resolutionAction;

    @Column(name = "resolved_by", nullable = false)
    private UUID resolvedBy;

    @Column(name = "notes")
    private String notes;

    @Column(name = "resolved_at", nullable = false)
    private Instant resolvedAt;

    public AuditDiscrepancyResolutionJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getAuditCycleId() { return auditCycleId; }
    public void setAuditCycleId(UUID auditCycleId) { this.auditCycleId = auditCycleId; }

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }

    public String getDiscrepancyType() { return discrepancyType; }
    public void setDiscrepancyType(String discrepancyType) { this.discrepancyType = discrepancyType; }

    public String getResolutionAction() { return resolutionAction; }
    public void setResolutionAction(String resolutionAction) { this.resolutionAction = resolutionAction; }

    public UUID getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(UUID resolvedBy) { this.resolvedBy = resolvedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
