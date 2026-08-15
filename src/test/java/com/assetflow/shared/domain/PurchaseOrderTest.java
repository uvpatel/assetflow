package com.assetflow.shared.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseOrderTest {

    @Test
    void shouldFollowPurchaseOrderStateTransitionsCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        PurchaseOrder po = PurchaseOrder.create(
                orgId,
                vendorId,
                "PO-2026-1001",
                new BigDecimal("12500.00")
        );

        assertThat(po.getId()).isNotNull();
        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
        assertThat(po.getTotalAmount()).isEqualTo(new BigDecimal("12500.00"));

        po.issue();
        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.ISSUED);

        po.fulfill();
        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.FULFILLED);
    }

    @Test
    void shouldRejectInvalidStateTransitions() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        PurchaseOrder po = PurchaseOrder.create(orgId, vendorId, "PO-2026-1002", new BigDecimal("5000.00"));

        assertThatThrownBy(po::fulfill)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only ISSUED purchase orders can be fulfilled");

        po.issue();
        po.fulfill();

        assertThatThrownBy(po::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FULFILLED purchase orders cannot be cancelled");
    }

    @Test
    void shouldRejectNegativeTotalAmount() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        assertThatThrownBy(() -> PurchaseOrder.create(orgId, vendorId, "PO-BAD", new BigDecimal("-100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
}
