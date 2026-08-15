package com.assetflow.shared.domain;

import java.time.Instant;
import java.util.UUID;

public class FeatureFlag {
    private final UUID id;
    private final UUID organizationId;
    private final String flagKey;
    private String description;
    private boolean isEnabled;
    private final Instant createdAt;
    private Instant updatedAt;

    public FeatureFlag(
            UUID id,
            UUID organizationId,
            String flagKey,
            String description,
            boolean isEnabled,
            Instant createdAt,
            Instant updatedAt) {
        if (flagKey == null || flagKey.isBlank()) {
            throw new IllegalArgumentException("Flag key cannot be blank");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.flagKey = flagKey.trim().toLowerCase();
        this.description = description;
        this.isEnabled = isEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FeatureFlag create(
            UUID organizationId,
            String flagKey,
            String description,
            boolean isEnabled) {
        Instant now = Instant.now();
        return new FeatureFlag(
                UUID.randomUUID(),
                organizationId,
                flagKey,
                description,
                isEnabled,
                now,
                now
        );
    }

    public void toggle(boolean enabled) {
        this.isEnabled = enabled;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getFlagKey() { return flagKey; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return isEnabled; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
