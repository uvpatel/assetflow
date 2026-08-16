package com.assetflow.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class UserAccount {

    public static final int DEFAULT_MAX_FAILED_ATTEMPTS = 5;
    public static final long DEFAULT_LOCKOUT_DURATION_SECONDS = 900L; // 15 minutes

    private final UUID id;
    private final String email;
    private String passwordHash;
    private final UUID organizationId;
    private UserStatus status;
    private UserRole role;
    private int failedLoginAttempts;
    private Instant lockedUntil;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserAccount(UUID id, String email, String passwordHash, UUID organizationId,
                       UserStatus status, UserRole role, Instant createdAt, Instant updatedAt) {
        this(id, email, passwordHash, organizationId, status, role, 0, null, createdAt, updatedAt);
    }

    public UserAccount(UUID id, String email, String passwordHash, UUID organizationId,
                       UserStatus status, UserRole role, int failedLoginAttempts, Instant lockedUntil,
                       Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null").trim().toLowerCase();
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.failedLoginAttempts = Math.max(0, failedLoginAttempts);
        this.lockedUntil = lockedUntil;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static UserAccount createEmployee(String email, String passwordHash, UUID organizationId) {
        UUID newId = UUID.randomUUID();
        Instant now = Instant.now();
        return new UserAccount(newId, email, passwordHash, organizationId, UserStatus.ACTIVE, UserRole.EMPLOYEE, 0, null, now, now);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserRole getRole() {
        return role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateRole(UserRole newRole) {
        this.role = Objects.requireNonNull(newRole, "New role cannot be null");
        this.updatedAt = Instant.now();
    }

    public void updateStatus(UserStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "New status cannot be null");
        this.updatedAt = Instant.now();
    }

    public void updatePasswordHash(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "New password hash cannot be null");
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isLocked(Instant now) {
        return this.lockedUntil != null && this.lockedUntil.isAfter(now);
    }

    public void recordFailedLogin(Instant now, int maxAttempts, long lockoutDurationSeconds) {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= maxAttempts) {
            this.lockedUntil = now.plusSeconds(lockoutDurationSeconds);
        }
        this.updatedAt = now;
    }

    public void recordSuccessfulLogin(Instant now) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = now;
    }

    public void unlock(Instant now) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = now;
    }
}
