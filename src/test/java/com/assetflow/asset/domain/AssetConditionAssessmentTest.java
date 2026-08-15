package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetConditionAssessmentTest {

    @Test
    void shouldRecordConditionAssessmentCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID assessorId = UUID.randomUUID();

        AssetConditionAssessment assessment = AssetConditionAssessment.assess(
                orgId,
                assetId,
                assessorId,
                8,
                "Minor cosmetic wear, excellent functional state"
        );

        assertThat(assessment.getId()).isNotNull();
        assertThat(assessment.getConditionScore()).isEqualTo(8);
        assertThat(assessment.getAssessmentNotes()).contains("cosmetic wear");
        assertThat(assessment.getAssessedAt()).isNotNull();
    }

    @Test
    void shouldRejectInvalidConditionScores() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID assessorId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetConditionAssessment.assess(orgId, assetId, assessorId, 0, "Too low"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between 1 and 10");

        assertThatThrownBy(() -> AssetConditionAssessment.assess(orgId, assetId, assessorId, 11, "Too high"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between 1 and 10");
    }
}
