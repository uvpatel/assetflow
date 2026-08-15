package com.assetflow.shared.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendorPerformanceEvaluationTest {

    @Test
    void shouldCreateEvaluationAndCalculateAverageRatingCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID evaluatorId = UUID.randomUUID();

        VendorPerformanceEvaluation eval = VendorPerformanceEvaluation.evaluate(
                orgId,
                vendorId,
                evaluatorId,
                5,
                4,
                3,
                "On-time delivery, good hardware quality"
        );

        assertThat(eval.getId()).isNotNull();
        assertThat(eval.getDeliveryScore()).isEqualTo(5);
        assertThat(eval.getQualityScore()).isEqualTo(4);
        assertThat(eval.getCommunicationScore()).isEqualTo(3);
        assertThat(eval.getOverallRating()).isEqualTo(new BigDecimal("4.00"));
    }

    @Test
    void shouldRejectScoresOutsideValidRange() {
        UUID orgId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID evaluatorId = UUID.randomUUID();

        assertThatThrownBy(() -> VendorPerformanceEvaluation.evaluate(
                orgId, vendorId, evaluatorId, 0, 4, 3, "Too low"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be between 1 and 5");

        assertThatThrownBy(() -> VendorPerformanceEvaluation.evaluate(
                orgId, vendorId, evaluatorId, 5, 6, 3, "Too high"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be between 1 and 5");
    }
}
