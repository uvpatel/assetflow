package com.assetflow.reporting.domain;

import java.time.Instant;
import java.util.UUID;

public class SavedSearchPreset {
    private final UUID id;
    private final UUID organizationId;
    private final UUID userId;
    private String name;
    private final String targetEntity;
    private String filterJson;
    private boolean isShared;
    private final Instant createdAt;
    private Instant updatedAt;

    public SavedSearchPreset(
            UUID id,
            UUID organizationId,
            UUID userId,
            String name,
            String targetEntity,
            String filterJson,
            boolean isShared,
            Instant createdAt,
            Instant updatedAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Preset name cannot be blank");
        }
        if (filterJson == null || filterJson.isBlank()) {
            throw new IllegalArgumentException("Filter JSON cannot be empty");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.userId = userId;
        this.name = name;
        this.targetEntity = targetEntity;
        this.filterJson = filterJson;
        this.isShared = isShared;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SavedSearchPreset create(
            UUID organizationId,
            UUID userId,
            String name,
            String targetEntity,
            String filterJson,
            boolean isShared) {
        Instant now = Instant.now();
        return new SavedSearchPreset(
                UUID.randomUUID(),
                organizationId,
                userId,
                name,
                targetEntity,
                filterJson,
                isShared,
                now,
                now
        );
    }

    public void update(String name, String filterJson, boolean isShared) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Preset name cannot be blank");
        }
        if (filterJson == null || filterJson.isBlank()) {
            throw new IllegalArgumentException("Filter JSON cannot be empty");
        }
        this.name = name;
        this.filterJson = filterJson;
        this.isShared = isShared;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getTargetEntity() { return targetEntity; }
    public String getFilterJson() { return filterJson; }
    public boolean isShared() { return isShared; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
