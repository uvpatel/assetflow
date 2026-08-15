package com.assetflow.reporting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SavedSearchPresetTest {

    @Test
    @DisplayName("Should create and update search preset correctly")
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
        assertThat(preset.isDefault()).isFalse();

        preset.update("Critical Overdue Laptops", "{\"status\":\"ALLOCATED\",\"overdue\":true,\"priority\":\"HIGH\"}", false);
        assertThat(preset.getName()).isEqualTo("Critical Overdue Laptops");
        assertThat(preset.isShared()).isFalse();
    }

    @Test
    @DisplayName("Should toggle default status on preset")
    void shouldToggleDefaultStatus() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SavedSearchPreset preset = SavedSearchPreset.create(
                orgId,
                userId,
                "Default Asset View",
                "ASSET",
                "{\"status\":\"ACTIVE\"}",
                false
        );

        preset.markDefault();
        assertThat(preset.isDefault()).isTrue();

        preset.unmarkDefault();
        assertThat(preset.isDefault()).isFalse();
    }

    @Test
    @DisplayName("Should reject blank name or empty filter")
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
