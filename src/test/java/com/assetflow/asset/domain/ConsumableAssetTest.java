package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsumableAssetTest {

    @Test
    void shouldRestockAndDisburseStockCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ConsumableAsset asset = ConsumableAsset.create(
                orgId,
                "Ethernet Cable 10m",
                "SKU-ETH-10M",
                categoryId,
                50,
                10,
                "METERS",
                new BigDecimal("12.50")
        );

        assertThat(asset.getQuantityOnHand()).isEqualTo(50);
        assertThat(asset.isBelowThreshold()).isFalse();

        asset.disburse(45);
        assertThat(asset.getQuantityOnHand()).isEqualTo(5);
        assertThat(asset.isBelowThreshold()).isTrue();

        asset.restock(20);
        assertThat(asset.getQuantityOnHand()).isEqualTo(25);
        assertThat(asset.isBelowThreshold()).isFalse();
    }

    @Test
    void shouldRejectDisburseExcessQuantity() {
        UUID orgId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ConsumableAsset asset = ConsumableAsset.create(
                orgId,
                "Printer Paper A4",
                "SKU-PAPER-A4",
                categoryId,
                10,
                5,
                "REAM",
                new BigDecimal("4.00")
        );

        assertThatThrownBy(() -> asset.disburse(15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock available");
    }
}
