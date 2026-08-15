package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetWarrantyTest {

    @Test
    void shouldCreateAndCalculateWarrantyExpiration() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant startDate = now.minus(100, ChronoUnit.DAYS);
        Instant endDate = now.plus(15, ChronoUnit.DAYS);

        AssetWarranty warranty = AssetWarranty.create(
                orgId,
                assetId,
                "AppleCare+",
                "POL-AC-9921",
                startDate,
                endDate,
                "Full hardware replacement & accidental damage"
        );

        assertThat(warranty.getId()).isNotNull();
        assertThat(warranty.isExpired(now)).isFalse();
        assertThat(warranty.isExpiringWithin(now, 30)).isTrue();
        assertThat(warranty.isExpiringWithin(now, 7)).isFalse();
    }

    @Test
    void shouldRejectInvalidDateRange() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        Instant now = Instant.now();

        assertThatThrownBy(() -> AssetWarranty.create(
                orgId,
                assetId,
                "Dell ProSupport",
                "POL-DELL-100",
                now,
                now.minus(10, ChronoUnit.DAYS),
                "Invalid dates"
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("end date cannot be before start date");
    }
}
