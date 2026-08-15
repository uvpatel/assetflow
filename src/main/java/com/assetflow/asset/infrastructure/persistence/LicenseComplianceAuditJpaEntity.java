package com.assetflow.asset.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "license_compliance_audits")
public class LicenseComplianceAuditJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "license_id", nullable = false)
    private UUID licenseId;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "allocated_seats", nullable = false)
    private int allocatedSeats;

    @Column(name = "compliance_status", nullable = false)
    private String complianceStatus;

    @Column(name = "audit_notes")
    private String auditNotes;

    @Column(name = "audited_at", nullable = false)
    private Instant auditedAt;

    public LicenseComplianceAuditJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getLicenseId() { return licenseId; }
    public void setLicenseId(UUID licenseId) { this.licenseId = licenseId; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAllocatedSeats() { return allocatedSeats; }
    public void setAllocatedSeats(int allocatedSeats) { this.allocatedSeats = allocatedSeats; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }

    public String getAuditNotes() { return auditNotes; }
    public void setAuditNotes(String auditNotes) { this.auditNotes = auditNotes; }

    public Instant getAuditedAt() { return auditedAt; }
    public void setAuditedAt(Instant auditedAt) { this.auditedAt = auditedAt; }
}
