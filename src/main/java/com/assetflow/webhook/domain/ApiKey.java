package com.assetflow.webhook.domain;

import java.time.Instant;
import java.util.UUID;

public class ApiKey {
    private final UUID id;
    private final UUID organizationId;
    private final String name;
    private final String keyPrefix;
    private final String keyHash;
    private final String scopes;
    private final Instant expiresAt;
    private boolean isRevoked;
    private final Instant createdAt;
    private Instant updatedAt;

    public ApiKey(
            UUID id,
            UUID organizationId,
            String name,
            String keyPrefix,
            String keyHash,
            String scopes,
            Instant expiresAt,
            boolean isRevoked,
            Instant createdAt,
            Instant updatedAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("API Key name cannot be blank");
        }
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new IllegalArgumentException("Key prefix cannot be blank");
        }
        if (keyHash == null || keyHash.isBlank()) {
            throw new IllegalArgumentException("Key hash cannot be blank");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.keyPrefix = keyPrefix;
        this.keyHash = keyHash;
        this.scopes = scopes;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ApiKey create(
            UUID organizationId,
            String name,
            String keyPrefix,
            String keyHash,
            String scopes,
            Instant expiresAt) {
        Instant now = Instant.now();
        return new ApiKey(
                UUID.randomUUID(),
                organizationId,
                name,
                keyPrefix,
                keyHash,
                scopes,
                expiresAt,
                false,
                now,
                now
        );
    }

    public boolean isValid(Instant now) {
        if (isRevoked) return false;
        if (expiresAt != null && now.isAfter(expiresAt)) return false;
        return true;
    }

    public void revoke() {
        this.isRevoked = true;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public String getKeyPrefix() { return keyPrefix; }
    public String getKeyHash() { return keyHash; }
    public String getScopes() { return scopes; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return isRevoked; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
