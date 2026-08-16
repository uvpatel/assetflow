package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsumableBatchTest {

    @Test
    @DisplayName("Should create batch, consume quantity, and evaluate expiry correctly")
    void shouldCreateConsumeAndEvaluateExpiry() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        Instant expiry = Instant.now().plus(15, ChronoUnit.DAYS);

        ConsumableBatch batch = ConsumableBatch.create(orgId, assetId, "BATCH-2026-A", 100, expiry);

        assertThat(batch.getId()).isNotNull();
        assertThat(batch.getCurrentQuantity()).isEqualTo(100);
        assertThat(batch.isExpired(Instant.now())).isFalse();
        assertThat(batch.isExpiringWithin(Instant.now(), 30)).isTrue();

        batch.consume(25);
        assertThat(batch.getCurrentQuantity()).isEqualTo(75);
    }

    @Test
    @DisplayName("Should reject over-consumption beyond available batch quantity")
    void shouldRejectOverConsumption() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        ConsumableBatch batch = ConsumableBatch.create(orgId, assetId, "BATCH-2026-B", 10, Instant.now().plus(60, ChronoUnit.DAYS));

        assertThatThrownBy(() -> batch.consume(15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient quantity");
    }
}
