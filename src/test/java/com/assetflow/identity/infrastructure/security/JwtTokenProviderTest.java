package com.assetflow.identity.infrastructure.security;

import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256-security!");
        jwtProperties.setAccessTokenExpirationSeconds(3600);
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UserAccount user = new UserAccount(
                userId, "test@example.com", "hash", orgId,
                UserStatus.ACTIVE, UserRole.ADMIN, Instant.now(), Instant.now()
        );

        String token = jwtTokenProvider.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
        assertThat(jwtTokenProvider.getOrganizationIdFromToken(token)).isEqualTo(orgId);
        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo("ADMIN");
    }

    @Test
    void shouldRejectInvalidOrTamperedToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.token";
        assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    void shouldExtractClaimsCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UserAccount user = new UserAccount(
                userId, "employee@example.com", "hash", orgId,
                UserStatus.ACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now()
        );

        String token = jwtTokenProvider.generateAccessToken(user);
        Claims claims = jwtTokenProvider.getClaims(token);

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("email", String.class)).isEqualTo("employee@example.com");
        assertThat(claims.get("organizationId", String.class)).isEqualTo(orgId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("EMPLOYEE");
    }
}
