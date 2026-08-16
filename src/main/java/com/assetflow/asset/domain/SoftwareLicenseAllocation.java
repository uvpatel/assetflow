package com.assetflow.asset.domain;

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
@Table(name = "software_license_allocations")
public class SoftwareLicenseAllocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "software_license_id", nullable = false)
    private UUID softwareLicenseId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "allocated_at", nullable = false)
    private Instant allocatedAt = Instant.now();

    @Column(nullable = false)
    private String status = "ACTIVE";

    public SoftwareLicenseAllocation() {
    }

    public SoftwareLicenseAllocation(UUID organizationId, UUID softwareLicenseId, UUID employeeId) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.softwareLicenseId = Objects.requireNonNull(softwareLicenseId, "Software License ID cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.allocatedAt = Instant.now();
        this.status = "ACTIVE";
    }

    public void revoke() {
        this.status = "REVOKED";
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getSoftwareLicenseId() { return softwareLicenseId; }
    public UUID getEmployeeId() { return employeeId; }
    public Instant getAllocatedAt() { return allocatedAt; }
    public String getStatus() { return status; }
}
