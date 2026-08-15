package com.assetflow.shared.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeatureFlagTest {

    @Test
    void shouldCreateAndToggleFeatureFlagCorrectly() {
        UUID orgId = UUID.randomUUID();

        FeatureFlag flag = FeatureFlag.create(
                orgId,
                "ENABLE_PDF_EXPORT_V2",
                "Enables enhanced multi-page PDF export engine",
                false
        );

        assertThat(flag.getId()).isNotNull();
        assertThat(flag.getFlagKey()).isEqualTo("enable_pdf_export_v2");
        assertThat(flag.isEnabled()).isFalse();

        flag.toggle(true);

        assertThat(flag.isEnabled()).isTrue();
    }

    @Test
    void shouldRejectBlankFlagKey() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> FeatureFlag.create(orgId, "   ", "Description", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be blank");
    }
}
