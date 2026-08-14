package com.assetflow.booking.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_bookings")
public class ResourceBooking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "booked_by_employee_id", nullable = false)
    private UUID bookedByEmployeeId;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private String status = "CONFIRMED";

    public ResourceBooking() {
    }

    public ResourceBooking(UUID organizationId, UUID resourceId, UUID bookedByEmployeeId, String title, Instant startTime, Instant endTime) {
        this.organizationId = organizationId;
        this.resourceId = resourceId;
        this.bookedByEmployeeId = bookedByEmployeeId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "CONFIRMED";
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

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public UUID getBookedByEmployeeId() {
        return bookedByEmployeeId;
    }

    public void setBookedByEmployeeId(UUID bookedByEmployeeId) {
        this.bookedByEmployeeId = bookedByEmployeeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
