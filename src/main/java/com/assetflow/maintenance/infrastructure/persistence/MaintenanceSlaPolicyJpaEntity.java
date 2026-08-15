package com.assetflow.maintenance.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "maintenance_sla_policies")
public class MaintenanceSlaPolicyJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "response_deadline_hours", nullable = false)
    private int responseDeadlineHours;

    @Column(name = "resolution_deadline_hours", nullable = false)
    private int resolutionDeadlineHours;

    @Column(name = "escalation_email")
    private String escalationEmail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public MaintenanceSlaPolicyJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getResponseDeadlineHours() { return responseDeadlineHours; }
    public void setResponseDeadlineHours(int responseDeadlineHours) { this.responseDeadlineHours = responseDeadlineHours; }

    public int getResolutionDeadlineHours() { return resolutionDeadlineHours; }
    public void setResolutionDeadlineHours(int resolutionDeadlineHours) { this.resolutionDeadlineHours = resolutionDeadlineHours; }

    public String getEscalationEmail() { return escalationEmail; }
    public void setEscalationEmail(String escalationEmail) { this.escalationEmail = escalationEmail; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
