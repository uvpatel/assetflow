package com.assetflow.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseRequisitionItemTest {

    @Test
    @DisplayName("Should create item and calculate total price")
    void shouldCalculateTotal() {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();

        PurchaseRequisitionItem item = new PurchaseRequisitionItem(
                orgId,
                reqId,
                "Dell 27-inch 4K Monitor",
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                3
        );

        assertThat(item.getItemDescription()).isEqualTo("Dell 27-inch 4K Monitor");
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.calculateTotal()).isEqualByComparingTo(new BigDecimal("1050.00"));
    }
}
