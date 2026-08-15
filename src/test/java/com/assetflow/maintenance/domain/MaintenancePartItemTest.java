package com.assetflow.maintenance.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenancePartItemTest {

    @Test
    void shouldCreatePartItemAndCalculateTotalCostCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID maintenanceId = UUID.randomUUID();

        MaintenancePartItem partItem = MaintenancePartItem.create(
                orgId,
                maintenanceId,
                "RAM Module 16GB DDR4",
                "MEM-16G-001",
                2,
                new BigDecimal("75.50")
        );

        assertThat(partItem.getId()).isNotNull();
        assertThat(partItem.getPartName()).isEqualTo("RAM Module 16GB DDR4");
        assertThat(partItem.getQuantityUsed()).isEqualTo(2);
        assertThat(partItem.getUnitCost()).isEqualTo(new BigDecimal("75.50"));
        assertThat(partItem.getTotalCost()).isEqualTo(new BigDecimal("151.00"));
    }

    @Test
    void shouldRejectInvalidQuantityOrUnitCost() {
        UUID orgId = UUID.randomUUID();
        UUID maintenanceId = UUID.randomUUID();

        assertThatThrownBy(() -> MaintenancePartItem.create(orgId, maintenanceId, "Screen", "SCR-01", 0, new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");

        assertThatThrownBy(() -> MaintenancePartItem.create(orgId, maintenanceId, "Screen", "SCR-01", 1, new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
}
