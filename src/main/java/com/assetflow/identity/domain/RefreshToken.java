package com.assetflow.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant createdAt;
    private boolean revoked;
    private Instant revokedAt;
    private String replacedByTokenHash;

    public RefreshToken(UUID id, UUID userId, String tokenHash, Instant expiresAt,
                        Instant createdAt, boolean revoked, Instant revokedAt, String replacedByTokenHash) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "Token hash cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.revoked = revoked;
        this.revokedAt = revokedAt;
        this.replacedByTokenHash = replacedByTokenHash;
    }

    public static RefreshToken create(UUID userId, String tokenHash, Instant expiresAt) {
        return new RefreshToken(UUID.randomUUID(), userId, tokenHash, expiresAt, Instant.now(), false, null, null);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public String getReplacedByTokenHash() {
        return replacedByTokenHash;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public void revoke(String replacementHash, Instant now) {
        this.revoked = true;
        this.revokedAt = now != null ? now : Instant.now();
        this.replacedByTokenHash = replacementHash;
    }

    public void revoke(Instant now) {
        revoke(null, now);
    }
}
