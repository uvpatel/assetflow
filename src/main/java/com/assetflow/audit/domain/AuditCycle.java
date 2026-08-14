package com.assetflow.audit.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_cycles")
public class AuditCycle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String status = "OPEN";

    @Column(name = "closed_at")
    private Instant closedAt;

    public AuditCycle() {
    }

    public AuditCycle(UUID organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.status = "OPEN";
    }

    public void close() {
        this.status = "CLOSED";
        this.closedAt = Instant.now();
    }

    public boolean isClosed() {
        return "CLOSED".equals(this.status);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }
}
