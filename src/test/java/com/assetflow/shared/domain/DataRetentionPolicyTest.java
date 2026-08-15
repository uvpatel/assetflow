package com.assetflow.shared.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataRetentionPolicyTest {

    @Test
    void shouldCreateAndCalculateRetentionCutoffCorrectly() {
        UUID orgId = UUID.randomUUID();
        Instant refTime = Instant.now();

        DataRetentionPolicy policy = DataRetentionPolicy.create(
                orgId,
                "ACTIVITY_LOG",
                90,
                DataRetentionPolicy.RetentionAction.PURGE
        );

        assertThat(policy.getId()).isNotNull();
        assertThat(policy.getTargetModule()).isEqualTo("ACTIVITY_LOG");
        assertThat(policy.getRetentionDays()).isEqualTo(90);
        assertThat(policy.getAction()).isEqualTo(DataRetentionPolicy.RetentionAction.PURGE);
        assertThat(policy.isActive()).isTrue();

        Instant cutoff = policy.calculateCutoffTimestamp(refTime);
        assertThat(cutoff).isEqualTo(refTime.minus(90, ChronoUnit.DAYS));
    }

    @Test
    void shouldRejectInvalidRetentionDays() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> DataRetentionPolicy.create(orgId, "AUDIT", 0, DataRetentionPolicy.RetentionAction.ARCHIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }
}
