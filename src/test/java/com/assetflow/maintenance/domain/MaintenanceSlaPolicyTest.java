package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenanceSlaPolicyTest {

    @Test
    void shouldCalculateDeadlinesAndDetectBreachCorrectly() {
        UUID orgId = UUID.randomUUID();
        Instant startTime = Instant.now();

        MaintenanceSlaPolicy policy = MaintenanceSlaPolicy.create(
                orgId,
                MaintenanceSlaPolicy.Priority.CRITICAL,
                2,
                8,
                "escalations@assetflow.io"
        );

        assertThat(policy.getId()).isNotNull();
        assertThat(policy.calculateResponseDeadline(startTime)).isEqualTo(startTime.plus(2, ChronoUnit.HOURS));
        assertThat(policy.calculateResolutionDeadline(startTime)).isEqualTo(startTime.plus(8, ChronoUnit.HOURS));

        Instant earlyCompletion = startTime.plus(5, ChronoUnit.HOURS);
        assertThat(policy.isBreached(startTime, earlyCompletion)).isFalse();

        Instant lateCompletion = startTime.plus(9, ChronoUnit.HOURS);
        assertThat(policy.isBreached(startTime, lateCompletion)).isTrue();
    }

    @Test
    void shouldRejectInvalidDeadlines() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> MaintenanceSlaPolicy.create(orgId, MaintenanceSlaPolicy.Priority.HIGH, 0, 5, "a@b.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive numbers");

        assertThatThrownBy(() -> MaintenanceSlaPolicy.create(orgId, MaintenanceSlaPolicy.Priority.HIGH, 10, 4, "a@b.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("shorter than response deadline");
    }
}
