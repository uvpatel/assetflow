package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetMaintenanceScheduleTest {

    @Test
    void shouldCreateAndPerformMaintenanceScheduleCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        Instant firstDue = Instant.now().plus(30, ChronoUnit.DAYS);

        AssetMaintenanceSchedule schedule = AssetMaintenanceSchedule.create(
                orgId,
                assetId,
                "HVAC Filter Replacement",
                90,
                firstDue
        );

        assertThat(schedule.getId()).isNotNull();
        assertThat(schedule.getStatus()).isEqualTo(AssetMaintenanceSchedule.ScheduleStatus.ACTIVE);
        assertThat(schedule.getFrequencyDays()).isEqualTo(90);

        Instant performedAt = Instant.now();
        schedule.markPerformed(performedAt);

        assertThat(schedule.getLastPerformedAt()).isEqualTo(performedAt);
        assertThat(schedule.getNextDueDate()).isEqualTo(performedAt.plus(90, ChronoUnit.DAYS));
    }
}
