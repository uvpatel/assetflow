package com.assetflow.audit.domain;

import java.time.Instant;
import java.util.UUID;

public class AuditDiscrepancyResolution {
    private final UUID id;
    private final UUID organizationId;
    private final UUID auditCycleId;
    private final UUID assetId;
    private final DiscrepancyType discrepancyType;
    private final ResolutionAction resolutionAction;
    private final UUID resolvedBy;
    private final String notes;
    private final Instant resolvedAt;

    public enum DiscrepancyType {
        MISSING_ASSET,
        LOCATION_MISMATCH,
        CONDITION_MISMATCH,
        UNAUTHORIZED_TRANSFER
    }

    public enum ResolutionAction {
        MARK_AS_LOST,
        UPDATE_LOCATION,
        SCHEDULE_MAINTENANCE,
        ACCEPT_DISCREPANCY
    }

    public AuditDiscrepancyResolution(
            UUID id,
            UUID organizationId,
            UUID auditCycleId,
            UUID assetId,
            DiscrepancyType discrepancyType,
            ResolutionAction resolutionAction,
            UUID resolvedBy,
            String notes,
            Instant resolvedAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.auditCycleId = auditCycleId;
        this.assetId = assetId;
        this.discrepancyType = discrepancyType;
        this.resolutionAction = resolutionAction;
        this.resolvedBy = resolvedBy;
        this.notes = notes;
        this.resolvedAt = resolvedAt;
    }

    public static AuditDiscrepancyResolution resolve(
            UUID organizationId,
            UUID auditCycleId,
            UUID assetId,
            DiscrepancyType type,
            ResolutionAction action,
            UUID resolvedBy,
            String notes) {
        return new AuditDiscrepancyResolution(
                UUID.randomUUID(),
                organizationId,
                auditCycleId,
                assetId,
                type,
                action,
                resolvedBy,
                notes,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAuditCycleId() { return auditCycleId; }
    public UUID getAssetId() { return assetId; }
    public DiscrepancyType getDiscrepancyType() { return discrepancyType; }
    public ResolutionAction getResolutionAction() { return resolutionAction; }
    public UUID getResolvedBy() { return resolvedBy; }
    public String getNotes() { return notes; }
    public Instant getResolvedAt() { return resolvedAt; }
}
