package com.assetflow.booking.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class CreateBookingRequest {

    @NotNull(message = "Resource ID is required")
    private UUID resourceId;

    @NotNull(message = "Employee ID is required")
    private UUID bookedByEmployeeId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(UUID resourceId, UUID bookedByEmployeeId, String title, Instant startTime, Instant endTime) {
        this.resourceId = resourceId;
        this.bookedByEmployeeId = bookedByEmployeeId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
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
}
