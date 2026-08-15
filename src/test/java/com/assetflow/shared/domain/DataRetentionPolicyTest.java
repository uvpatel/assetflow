package com.assetflow.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataRetentionPolicyTest {

    @Test
    @DisplayName("Should create policy and calculate cutoff timestamp correctly")
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
    @DisplayName("Should update execution tracking stats on markExecuted")
    void shouldUpdateExecutionTrackingOnMarkExecuted() {
        UUID orgId = UUID.randomUUID();
        DataRetentionPolicy policy = DataRetentionPolicy.create(
                orgId,
                "OUTBOX",
                30,
                DataRetentionPolicy.RetentionAction.ARCHIVE
        );

        policy.markExecuted(150);

        assertThat(policy.getRecordsProcessed()).isEqualTo(150);
        assertThat(policy.getLastExecutedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should reject invalid retention days")
    void shouldRejectInvalidRetentionDays() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> DataRetentionPolicy.create(orgId, "AUDIT", 0, DataRetentionPolicy.RetentionAction.ARCHIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }
}
