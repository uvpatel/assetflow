package com.assetflow.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendorContractTest {

    @Test
    @DisplayName("Should create, renew contract, and evaluate renewal notice threshold correctly")
    void shouldCreateAndRenewContractCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        VendorContract contract = VendorContract.create(
                orgId,
                vendorId,
                "CNT-2026-001",
                "SLA_MAINTENANCE",
                startDate,
                endDate,
                new BigDecimal("50000.00"),
                false,
                30
        );

        assertThat(contract.getId()).isNotNull();
        assertThat(contract.getContractNumber()).isEqualTo("CNT-2026-001");
        assertThat(contract.getStatus()).isEqualTo(VendorContract.ContractStatus.ACTIVE);
        assertThat(contract.isExpired(LocalDate.of(2027, 1, 1))).isTrue();
        assertThat(contract.isExpiringWithinDays(LocalDate.of(2026, 12, 1), 30)).isTrue();
        assertThat(contract.shouldSendRenewalNotice(LocalDate.of(2026, 12, 5))).isTrue();

        LocalDate newEndDate = LocalDate.of(2027, 12, 31);
        contract.renewContract(newEndDate, new BigDecimal("55000.00"));

        assertThat(contract.getEndDate()).isEqualTo(newEndDate);
        assertThat(contract.getContractValue()).isEqualTo(new BigDecimal("55000.00"));
        assertThat(contract.getStatus()).isEqualTo(VendorContract.ContractStatus.RENEWED);
    }

    @Test
    @DisplayName("Should reject invalid contract end dates or negative financial values")
    void shouldRejectInvalidDatesOrNegativeValues() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate invalidEndDate = LocalDate.of(2026, 5, 1);

        assertThatThrownBy(() -> VendorContract.create(
                orgId, vendorId, "CNT-BAD", "HARDWARE", startDate, invalidEndDate, new BigDecimal("1000.00"), false
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("on or after start date");

        assertThatThrownBy(() -> VendorContract.create(
                orgId, vendorId, "CNT-BAD", "HARDWARE", startDate, startDate, new BigDecimal("-1.00"), false
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be negative");
    }
}
