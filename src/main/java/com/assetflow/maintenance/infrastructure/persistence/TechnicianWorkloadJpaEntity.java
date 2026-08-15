package com.assetflow.maintenance.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "technician_workloads")
public class TechnicianWorkloadJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "technician_employee_id", nullable = false)
    private UUID technicianEmployeeId;

    @Column(name = "active_task_count", nullable = false)
    private int activeTaskCount;

    @Column(name = "max_task_capacity", nullable = false)
    private int maxTaskCapacity;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TechnicianWorkloadJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getTechnicianEmployeeId() { return technicianEmployeeId; }
    public void setTechnicianEmployeeId(UUID technicianEmployeeId) { this.technicianEmployeeId = technicianEmployeeId; }

    public int getActiveTaskCount() { return activeTaskCount; }
    public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }

    public int getMaxTaskCapacity() { return maxTaskCapacity; }
    public void setMaxTaskCapacity(int maxTaskCapacity) { this.maxTaskCapacity = maxTaskCapacity; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
