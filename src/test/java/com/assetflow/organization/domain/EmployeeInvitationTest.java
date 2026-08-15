package com.assetflow.organization.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeInvitationTest {

    @Test
    @DisplayName("Should create valid pending employee invitation")
    void shouldCreateValidInvitation() {
        UUID orgId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                orgId,
                "user@example.com",
                "EMPLOYEE",
                null,
                "token-123",
                expiresAt
        );

        assertThat(invitation.getOrganizationId()).isEqualTo(orgId);
        assertThat(invitation.getEmail()).isEqualTo("user@example.com");
        assertThat(invitation.getRole()).isEqualTo("EMPLOYEE");
        assertThat(invitation.getStatus()).isEqualTo(EmployeeInvitation.InvitationStatus.PENDING);
        assertThat(invitation.isExpired(Instant.now())).isFalse();
    }

    @Test
    @DisplayName("Should transition status to ACCEPTED on accept")
    void shouldAcceptInvitation() {
        UUID orgId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                orgId,
                "user@example.com",
                "EMPLOYEE",
                null,
                "token-123",
                expiresAt
        );

        invitation.accept(Instant.now());
        assertThat(invitation.getStatus()).isEqualTo(EmployeeInvitation.InvitationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should reject accept when invitation is expired")
    void shouldRejectAcceptWhenExpired() {
        UUID orgId = UUID.randomUUID();
        Instant expiresAt = Instant.now().minus(1, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                orgId,
                "user@example.com",
                "EMPLOYEE",
                null,
                "token-123",
                expiresAt
        );

        assertThatThrownBy(() -> invitation.accept(Instant.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("Should transition status to REVOKED on revoke")
    void shouldRevokeInvitation() {
        UUID orgId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                orgId,
                "user@example.com",
                "EMPLOYEE",
                null,
                "token-123",
                expiresAt
        );

        invitation.revoke();
        assertThat(invitation.getStatus()).isEqualTo(EmployeeInvitation.InvitationStatus.REVOKED);
    }
}
