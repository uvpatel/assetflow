package com.assetflow.maintenance.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class MaintenanceSlaPolicy {
    private final UUID id;
    private final UUID organizationId;
    private final Priority priority;
    private final int responseDeadlineHours;
    private final int resolutionDeadlineHours;
    private final String escalationEmail;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public MaintenanceSlaPolicy(
            UUID id,
            UUID organizationId,
            Priority priority,
            int responseDeadlineHours,
            int resolutionDeadlineHours,
            String escalationEmail,
            Instant createdAt,
            Instant updatedAt) {
        if (responseDeadlineHours <= 0 || resolutionDeadlineHours <= 0) {
            throw new IllegalArgumentException("SLA deadlines must be positive numbers");
        }
        if (resolutionDeadlineHours < responseDeadlineHours) {
            throw new IllegalArgumentException("Resolution deadline cannot be shorter than response deadline");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.priority = priority;
        this.responseDeadlineHours = responseDeadlineHours;
        this.resolutionDeadlineHours = resolutionDeadlineHours;
        this.escalationEmail = escalationEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MaintenanceSlaPolicy create(
            UUID organizationId,
            Priority priority,
            int responseDeadlineHours,
            int resolutionDeadlineHours,
            String escalationEmail) {
        Instant now = Instant.now();
        return new MaintenanceSlaPolicy(
                UUID.randomUUID(),
                organizationId,
                priority,
                responseDeadlineHours,
                resolutionDeadlineHours,
                escalationEmail,
                now,
                now
        );
    }

    public Instant calculateResponseDeadline(Instant startTime) {
        return startTime.plus(responseDeadlineHours, ChronoUnit.HOURS);
    }

    public Instant calculateResolutionDeadline(Instant startTime) {
        return startTime.plus(resolutionDeadlineHours, ChronoUnit.HOURS);
    }

    public boolean isBreached(Instant startTime, Instant completionTime) {
        Instant deadline = calculateResolutionDeadline(startTime);
        return completionTime.isAfter(deadline);
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public Priority getPriority() { return priority; }
    public int getResponseDeadlineHours() { return responseDeadlineHours; }
    public int getResolutionDeadlineHours() { return resolutionDeadlineHours; }
    public String getEscalationEmail() { return escalationEmail; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
