package com.assetflow.financial.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTaxWriteOffTest {

    @Test
    void shouldExecuteWriteOffAndDeductTaxBasisCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetTaxWriteOff writeOff = AssetTaxWriteOff.executeWriteOff(
                orgId,
                assetId,
                2026,
                new BigDecimal("10000.00"),
                new BigDecimal("2500.00"),
                "Accelerated tax depreciation write-off"
        );

        assertThat(writeOff.getId()).isNotNull();
        assertThat(writeOff.getFiscalYear()).isEqualTo(2026);
        assertThat(writeOff.getWriteoffAmount()).isEqualTo(new BigDecimal("2500.00"));
        assertThat(writeOff.getRemainingTaxBasis()).isEqualTo(new BigDecimal("7500.00"));
    }

    @Test
    void shouldRejectWriteOffExceedingCurrentTaxBasis() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetTaxWriteOff.executeWriteOff(
                orgId, assetId, 2026, new BigDecimal("1000.00"), new BigDecimal("1500.00"), "Excessive write-off"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot exceed current tax basis");
    }

    @Test
    void shouldRejectZeroOrNegativeWriteOffAmount() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetTaxWriteOff.executeWriteOff(
                orgId, assetId, 2026, new BigDecimal("1000.00"), new BigDecimal("0.00"), "Zero write-off"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("greater than zero");
    }
}
