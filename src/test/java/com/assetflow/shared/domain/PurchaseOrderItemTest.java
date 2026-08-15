package com.assetflow.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseOrderItemTest {

    @Test
    @DisplayName("Should initialize PO line item and calculate line total")
    void shouldInitializePoItemAndCalculateLineTotal() {
        UUID orgId = UUID.randomUUID();
        UUID poId = UUID.randomUUID();

        PurchaseOrderItem item = new PurchaseOrderItem(
                orgId,
                poId,
                "Dell XPS 15 Laptops",
                UUID.randomUUID(),
                new BigDecimal("1500.00"),
                5
        );

        assertThat(item.getItemDescription()).isEqualTo("Dell XPS 15 Laptops");
        assertThat(item.getQuantityOrdered()).isEqualTo(5);
        assertThat(item.getQuantityReceived()).isEqualTo(0);
        assertThat(item.getTotalPrice()).isEqualByComparingTo(new BigDecimal("7500.00"));
        assertThat(item.isFullyReceived()).isFalse();

        item.receiveQuantity(5);
        assertThat(item.getQuantityReceived()).isEqualTo(5);
        assertThat(item.isFullyReceived()).isTrue();
    }

    @Test
    @DisplayName("Should reject receiving more items than ordered")
    void shouldRejectReceivingMoreThanOrdered() {
        UUID orgId = UUID.randomUUID();
        UUID poId = UUID.randomUUID();

        PurchaseOrderItem item = new PurchaseOrderItem(
                orgId,
                poId,
                "Monitors",
                null,
                new BigDecimal("300.00"),
                2
        );

        item.receiveQuantity(2);
        assertThatThrownBy(() -> item.receiveQuantity(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot exceed quantity ordered");
    }
}
