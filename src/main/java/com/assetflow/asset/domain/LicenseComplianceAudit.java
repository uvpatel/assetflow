package com.assetflow.asset.domain;

import java.time.Instant;
import java.util.UUID;

public class LicenseComplianceAudit {
    private final UUID id;
    private final UUID organizationId;
    private final UUID licenseId;
    private final int totalSeats;
    private final int allocatedSeats;
    private final ComplianceStatus complianceStatus;
    private final String auditNotes;
    private final Instant auditedAt;

    public enum ComplianceStatus {
        COMPLIANT,
        UNDER_UTILIZED,
        AT_CAPACITY,
        OVER_ALLOCATED
    }

    public LicenseComplianceAudit(
            UUID id,
            UUID organizationId,
            UUID licenseId,
            int totalSeats,
            int allocatedSeats,
            ComplianceStatus complianceStatus,
            String auditNotes,
            Instant auditedAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.licenseId = licenseId;
        this.totalSeats = totalSeats;
        this.allocatedSeats = allocatedSeats;
        this.complianceStatus = complianceStatus;
        this.auditNotes = auditNotes;
        this.auditedAt = auditedAt;
    }

    public static LicenseComplianceAudit performAudit(
            UUID organizationId,
            UUID licenseId,
            int totalSeats,
            int allocatedSeats,
            String auditNotes) {
        ComplianceStatus status;
        if (allocatedSeats > totalSeats) {
            status = ComplianceStatus.OVER_ALLOCATED;
        } else if (allocatedSeats == totalSeats) {
            status = ComplianceStatus.AT_CAPACITY;
        } else if (totalSeats > 0 && ((double) allocatedSeats / totalSeats) < 0.5) {
            status = ComplianceStatus.UNDER_UTILIZED;
        } else {
            status = ComplianceStatus.COMPLIANT;
        }

        return new LicenseComplianceAudit(
                UUID.randomUUID(),
                organizationId,
                licenseId,
                totalSeats,
                allocatedSeats,
                status,
                auditNotes,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getLicenseId() { return licenseId; }
    public int getTotalSeats() { return totalSeats; }
    public int getAllocatedSeats() { return allocatedSeats; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public String getAuditNotes() { return auditNotes; }
    public Instant getAuditedAt() { return auditedAt; }
}
