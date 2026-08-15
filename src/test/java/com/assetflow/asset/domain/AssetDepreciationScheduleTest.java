package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetDepreciationScheduleTest {

    @Test
    void shouldCalculateStraightLineDepreciationCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                orgId,
                assetId,
                AssetDepreciationSchedule.DepreciationMethod.STRAIGHT_LINE,
                new BigDecimal("1200.00"),
                new BigDecimal("200.00"),
                10
        );

        assertThat(schedule.getCurrentBookValue()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(schedule.getAccumulatedDepreciation()).isEqualTo(BigDecimal.ZERO);

        schedule.calculateStraightLine(5);

        assertThat(schedule.getAccumulatedDepreciation()).isEqualTo(new BigDecimal("500.00"));
        assertThat(schedule.getCurrentBookValue()).isEqualTo(new BigDecimal("700.00"));
    }
}
