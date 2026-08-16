package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetMaintenanceScheduleTest {

    @Test
    @DisplayName("Should create and perform maintenance schedule correctly")
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

    @Test
    @DisplayName("Should pause and resume maintenance schedule correctly")
    void shouldPauseAndResumeSchedule() {
        UUID orgId = UUID.randomUUID();
        AssetMaintenanceSchedule schedule = AssetMaintenanceSchedule.create(
                orgId, UUID.randomUUID(), "Generator Inspection", 180, Instant.now().plus(10, ChronoUnit.DAYS)
        );

        schedule.pause();
        assertThat(schedule.getStatus()).isEqualTo(AssetMaintenanceSchedule.ScheduleStatus.PAUSED);

        schedule.resume();
        assertThat(schedule.getStatus()).isEqualTo(AssetMaintenanceSchedule.ScheduleStatus.ACTIVE);
    }
}
