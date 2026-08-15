package com.assetflow.reporting.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SavedSearchPresetTest {

    @Test
    void shouldCreateAndUpdatePresetCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SavedSearchPreset preset = SavedSearchPreset.create(
                orgId,
                userId,
                "Overdue Laptop Allocations",
                "ASSET",
                "{\"status\":\"ALLOCATED\",\"overdue\":true}",
                true
        );

        assertThat(preset.getId()).isNotNull();
        assertThat(preset.getName()).isEqualTo("Overdue Laptop Allocations");
        assertThat(preset.isShared()).isTrue();

        preset.update("Critical Overdue Laptops", "{\"status\":\"ALLOCATED\",\"overdue\":true,\"priority\":\"HIGH\"}", false);
        assertThat(preset.getName()).isEqualTo("Critical Overdue Laptops");
        assertThat(preset.isShared()).isFalse();
    }

    @Test
    void shouldRejectBlankNameOrEmptyFilter() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> SavedSearchPreset.create(orgId, userId, "", "ASSET", "{}", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Preset name cannot be blank");

        assertThatThrownBy(() -> SavedSearchPreset.create(orgId, userId, "Valid Name", "ASSET", "  ", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Filter JSON cannot be empty");
    }
}
