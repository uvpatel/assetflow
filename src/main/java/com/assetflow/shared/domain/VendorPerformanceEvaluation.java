package com.assetflow.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public class VendorPerformanceEvaluation {
    private final UUID id;
    private final UUID organizationId;
    private final UUID vendorId;
    private final UUID evaluatorUserId;
    private final int deliveryScore;
    private final int qualityScore;
    private final int communicationScore;
    private final BigDecimal overallRating;
    private final String evaluationNotes;
    private final Instant evaluatedAt;

    public VendorPerformanceEvaluation(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            UUID evaluatorUserId,
            int deliveryScore,
            int qualityScore,
            int communicationScore,
            BigDecimal overallRating,
            String evaluationNotes,
            Instant evaluatedAt) {
        validateScore("Delivery score", deliveryScore);
        validateScore("Quality score", qualityScore);
        validateScore("Communication score", communicationScore);

        this.id = id;
        this.organizationId = organizationId;
        this.vendorId = vendorId;
        this.evaluatorUserId = evaluatorUserId;
        this.deliveryScore = deliveryScore;
        this.qualityScore = qualityScore;
        this.communicationScore = communicationScore;
        this.overallRating = overallRating != null ? overallRating : calculateAverage(deliveryScore, qualityScore, communicationScore);
        this.evaluationNotes = evaluationNotes;
        this.evaluatedAt = evaluatedAt;
    }

    public static VendorPerformanceEvaluation evaluate(
            UUID organizationId,
            UUID vendorId,
            UUID evaluatorUserId,
            int deliveryScore,
            int qualityScore,
            int communicationScore,
            String evaluationNotes) {
        BigDecimal average = calculateAverage(deliveryScore, qualityScore, communicationScore);
        return new VendorPerformanceEvaluation(
                UUID.randomUUID(),
                organizationId,
                vendorId,
                evaluatorUserId,
                deliveryScore,
                qualityScore,
                communicationScore,
                average,
                evaluationNotes,
                Instant.now()
        );
    }

    private static void validateScore(String fieldName, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 5");
        }
    }

    private static BigDecimal calculateAverage(int s1, int s2, int s3) {
        double sum = s1 + s2 + s3;
        return BigDecimal.valueOf(sum / 3.0).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getVendorId() { return vendorId; }
    public UUID getEvaluatorUserId() { return evaluatorUserId; }
    public int getDeliveryScore() { return deliveryScore; }
    public int getQualityScore() { return qualityScore; }
    public int getCommunicationScore() { return communicationScore; }
    public BigDecimal getOverallRating() { return overallRating; }
    public String getEvaluationNotes() { return evaluationNotes; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
}
