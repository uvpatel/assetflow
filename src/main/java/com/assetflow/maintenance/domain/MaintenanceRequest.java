package com.assetflow.maintenance.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "requested_by_employee_id", nullable = false)
    private UUID requestedByEmployeeId;

    @Column(name = "assigned_technician_id")
    private UUID assignedTechnicianId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private String priority = "MEDIUM";

    @Column(name = "work_notes")
    private String workNotes;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public MaintenanceRequest() {
    }

    public MaintenanceRequest(UUID organizationId, UUID assetId, UUID requestedByEmployeeId, String title, String description, String priority) {
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.requestedByEmployeeId = requestedByEmployeeId;
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : "MEDIUM";
        this.status = "PENDING";
    }

    public void approve(UUID technicianId) {
        this.status = "APPROVED";
        this.assignedTechnicianId = technicianId;
    }

    public void resolve(String workNotes) {
        this.status = "RESOLVED";
        this.workNotes = workNotes;
        this.resolvedAt = Instant.now();
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

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public UUID getRequestedByEmployeeId() {
        return requestedByEmployeeId;
    }

    public void setRequestedByEmployeeId(UUID requestedByEmployeeId) {
        this.requestedByEmployeeId = requestedByEmployeeId;
    }

    public UUID getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(UUID assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getWorkNotes() {
        return workNotes;
    }

    public void setWorkNotes(String workNotes) {
        this.workNotes = workNotes;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
