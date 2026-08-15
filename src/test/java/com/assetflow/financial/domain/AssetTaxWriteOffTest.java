package com.assetflow.financial.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTaxWriteOffTest {

    @Test
    @DisplayName("Should execute write-off and auto-approve under $10,000 threshold")
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
        assertThat(writeOff.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.APPROVED);
    }

    @Test
    @DisplayName("Should require approval for write-offs equal to or exceeding $10,000 threshold")
    void shouldRequireApprovalForLargeWriteOff() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetTaxWriteOff writeOff = AssetTaxWriteOff.executeWriteOff(
                orgId,
                assetId,
                2026,
                new BigDecimal("20000.00"),
                new BigDecimal("10000.00"),
                "Server cluster write-off"
        );

        assertThat(writeOff.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.PENDING_APPROVAL);
    }

    @Test
    @DisplayName("Should transition status to APPROVED on approve")
    void shouldApproveWriteOff() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        AssetTaxWriteOff writeOff = AssetTaxWriteOff.executeWriteOff(
                orgId,
                assetId,
                2026,
                new BigDecimal("20000.00"),
                new BigDecimal("15000.00"),
                "Server cluster write-off"
        );

        writeOff.approve(approverId);

        assertThat(writeOff.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.APPROVED);
        assertThat(writeOff.getApprovedBy()).isEqualTo(approverId);
        assertThat(writeOff.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should reject write-off exceeding current tax basis")
    void shouldRejectWriteOffExceedingCurrentTaxBasis() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetTaxWriteOff.executeWriteOff(
                orgId, assetId, 2026, new BigDecimal("1000.00"), new BigDecimal("1500.00"), "Excessive write-off"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot exceed current tax basis");
    }

    @Test
    @DisplayName("Should reject zero or negative write-off amount")
    void shouldRejectZeroOrNegativeWriteOffAmount() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetTaxWriteOff.executeWriteOff(
                orgId, assetId, 2026, new BigDecimal("1000.00"), new BigDecimal("0.00"), "Zero write-off"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("greater than zero");
    }
}
