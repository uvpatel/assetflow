package com.assetflow.organization.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Location {

    private final UUID id;
    private final UUID organizationId;
    private String name;
    private final String code;
    private LocationType type;
    private UUID parentLocationId;
    private String address;
    private String description;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;

    public Location(UUID id, UUID organizationId, String name, String code,
                    LocationType type, UUID parentLocationId, String address,
                    String description, boolean active, Instant createdAt,
                    Instant updatedAt, long version) {
        this.id = Objects.requireNonNull(id, "Location ID cannot be null");
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.name = Objects.requireNonNull(name, "Location name cannot be null").trim();
        this.code = Objects.requireNonNull(code, "Location code cannot be null").trim().toUpperCase();
        this.type = Objects.requireNonNull(type, "Location type cannot be null");
        this.parentLocationId = parentLocationId;
        this.address = address != null ? address.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
        this.version = version;
    }

    public static Location create(UUID organizationId, String name, String code,
                                  LocationType type, UUID parentLocationId,
                                  String address, String description) {
        UUID newId = UUID.randomUUID();
        Instant now = Instant.now();
        return new Location(
                newId, organizationId, name, code, type,
                parentLocationId, address, description, true, now, now, 0L
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public LocationType getType() {
        return type;
    }

    public UUID getParentLocationId() {
        return parentLocationId;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public void updateDetails(String name, String address, String description) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        this.address = address != null ? address.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.updatedAt = Instant.now();
    }

    public void changeParent(UUID newParentLocationId) {
        if (this.id.equals(newParentLocationId)) {
            throw new IllegalArgumentException("A location cannot be its own parent");
        }
        this.parentLocationId = newParentLocationId;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }
}
