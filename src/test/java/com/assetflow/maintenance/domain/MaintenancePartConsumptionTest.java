package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenancePartConsumptionTest {

    @Test
    @DisplayName("Should create maintenance part item and calculate total cost accurately")
    void shouldCreatePartItemAndCalculateTotalCost() {
        UUID orgId = UUID.randomUUID();
        UUID maintId = UUID.randomUUID();
        UUID sparePartId = UUID.randomUUID();

        MaintenancePartItem item = MaintenancePartItem.create(
                orgId,
                maintId,
                sparePartId,
                "RAM 16GB DDR5 Module",
                "MEM-DDR5-16",
                4,
                new BigDecimal("75.50")
        );

        assertThat(item.getId()).isNotNull();
        assertThat(item.getSparePartId()).isEqualTo(sparePartId);
        assertThat(item.getPartName()).isEqualTo("RAM 16GB DDR5 Module");
        assertThat(item.getQuantityUsed()).isEqualTo(4);
        assertThat(item.getTotalCost()).isEqualByComparingTo(new BigDecimal("302.00"));
        assertThat(item.isInventoryDeducted()).isFalse();

        item.markInventoryDeducted();
        assertThat(item.isInventoryDeducted()).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid part name or quantity")
    void shouldRejectInvalidPartNameOrQuantity() {
        UUID orgId = UUID.randomUUID();
        UUID maintId = UUID.randomUUID();

        assertThatThrownBy(() -> MaintenancePartItem.create(orgId, maintId, "", "NUM-1", 2, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Part name cannot be blank");

        assertThatThrownBy(() -> MaintenancePartItem.create(orgId, maintId, "Filter", "NUM-2", 0, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity used must be greater than zero");
    }
}
