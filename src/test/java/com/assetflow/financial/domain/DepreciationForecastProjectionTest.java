package com.assetflow.financial.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepreciationForecastProjectionTest {

    @Test
    @DisplayName("Should create forecast projection entity with valid metrics")
    void shouldCreateForecastProjection() {
        UUID orgId = UUID.randomUUID();
        DepreciationForecastProjection projection = new DepreciationForecastProjection(
                orgId, 2027, new BigDecimal("12500.00"), new BigDecimal("45000.00")
        );

        assertThat(projection.getOrganizationId()).isEqualTo(orgId);
        assertThat(projection.getFiscalYear()).isEqualTo(2027);
        assertThat(projection.getProjectedDepreciation()).isEqualTo(new BigDecimal("12500.00"));
        assertThat(projection.getProjectedEndingBookValue()).isEqualTo(new BigDecimal("45000.00"));
    }

    @Test
    @DisplayName("Should reject invalid fiscal year below boundary")
    void shouldRejectInvalidFiscalYear() {
        UUID orgId = UUID.randomUUID();
        assertThatThrownBy(() -> new DepreciationForecastProjection(orgId, 1999, BigDecimal.ZERO, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid fiscal year");
    }
}
