package com.assetflow.webhook.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiKeyTest {

    @Test
    void shouldCreateAndValidateApiKeyCorrectly() {
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(30, ChronoUnit.DAYS);

        ApiKey apiKey = ApiKey.create(
                orgId,
                "CI Service Account",
                "af_12345678",
                "hash_abc123",
                "assets:read,assets:write",
                expiresAt
        );

        assertThat(apiKey.getId()).isNotNull();
        assertThat(apiKey.getName()).isEqualTo("CI Service Account");
        assertThat(apiKey.isValid(now)).isTrue();

        apiKey.revoke();
        assertThat(apiKey.isRevoked()).isTrue();
        assertThat(apiKey.isValid(now)).isFalse();
    }

    @Test
    void shouldDetectExpiredKey() {
        UUID orgId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant pastExpiration = now.minus(1, ChronoUnit.DAYS);

        ApiKey apiKey = ApiKey.create(
                orgId,
                "Expired Key",
                "af_87654321",
                "hash_xyz987",
                "read",
                pastExpiration
        );

        assertThat(apiKey.isValid(now)).isFalse();
    }

    @Test
    void shouldRejectBlankNameOrHash() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> ApiKey.create(orgId, "", "prefix", "hash", "read", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be blank");
    }
}
