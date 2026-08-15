package com.assetflow.organization.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "employee_invitations")
public class EmployeeInvitation extends BaseEntity {

    public enum InvitationStatus {
        PENDING,
        ACCEPTED,
        EXPIRED,
        REVOKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "invitation_token", nullable = false, unique = true)
    private String invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public EmployeeInvitation() {
    }

    public EmployeeInvitation(UUID organizationId, String email, String role, UUID departmentId,
                              String invitationToken, Instant expiresAt) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null").trim().toLowerCase();
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.departmentId = departmentId;
        this.invitationToken = Objects.requireNonNull(invitationToken, "Invitation token cannot be null");
        this.status = InvitationStatus.PENDING;
        this.expiresAt = Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
    }

    public void accept(Instant now) {
        if (status != InvitationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING invitations can be accepted");
        }
        if (isExpired(now)) {
            this.status = InvitationStatus.EXPIRED;
            throw new IllegalStateException("Invitation token has expired");
        }
        this.status = InvitationStatus.ACCEPTED;
    }

    public void revoke() {
        if (status != InvitationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING invitations can be revoked");
        }
        this.status = InvitationStatus.REVOKED;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
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

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
