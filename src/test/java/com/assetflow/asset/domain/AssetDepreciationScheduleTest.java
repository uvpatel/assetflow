package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetDepreciationScheduleTest {

    @Test
    @DisplayName("Should calculate straight line depreciation correctly")
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

        schedule.calculate(5);

        assertThat(schedule.getAccumulatedDepreciation()).isEqualTo(new BigDecimal("500.00"));
        assertThat(schedule.getCurrentBookValue()).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("Should calculate double declining balance depreciation and respect salvage value floor")
    void shouldCalculateDoubleDecliningBalance() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                orgId,
                assetId,
                AssetDepreciationSchedule.DepreciationMethod.DOUBLE_DECLINING_BALANCE,
                new BigDecimal("10000.00"),
                new BigDecimal("1000.00"),
                60 // 5 years
        );

        schedule.calculate(12);

        assertThat(schedule.getCurrentBookValue()).isLessThan(new BigDecimal("10000.00"));
        assertThat(schedule.getAccumulatedDepreciation()).isGreaterThan(BigDecimal.ZERO);
        assertThat(schedule.getCurrentBookValue()).isGreaterThanOrEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should calculate sum of years digits depreciation correctly")
    void shouldCalculateSumOfYearsDigits() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                orgId,
                assetId,
                AssetDepreciationSchedule.DepreciationMethod.SUM_OF_YEARS_DIGITS,
                new BigDecimal("15000.00"),
                new BigDecimal("3000.00"),
                36 // 3 years
        );

        schedule.calculate(12);

        assertThat(schedule.getCurrentBookValue()).isLessThan(new BigDecimal("15000.00"));
        assertThat(schedule.getAccumulatedDepreciation()).isGreaterThan(BigDecimal.ZERO);
        assertThat(schedule.getCurrentBookValue()).isGreaterThanOrEqualTo(new BigDecimal("3000.00"));
    }
}
