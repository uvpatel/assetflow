package com.assetflow.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vendor_performance_evaluations")
public class VendorPerformanceEvaluationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "evaluator_user_id", nullable = false)
    private UUID evaluatorUserId;

    @Column(name = "delivery_score", nullable = false)
    private int deliveryScore;

    @Column(name = "quality_score", nullable = false)
    private int qualityScore;

    @Column(name = "communication_score", nullable = false)
    private int communicationScore;

    @Column(name = "overall_rating", nullable = false)
    private BigDecimal overallRating;

    @Column(name = "evaluation_notes")
    private String evaluationNotes;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;

    public VendorPerformanceEvaluationJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getVendorId() { return vendorId; }
    public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }

    public UUID getEvaluatorUserId() { return evaluatorUserId; }
    public void setEvaluatorUserId(UUID evaluatorUserId) { this.evaluatorUserId = evaluatorUserId; }

    public int getDeliveryScore() { return deliveryScore; }
    public void setDeliveryScore(int deliveryScore) { this.deliveryScore = deliveryScore; }

    public int getQualityScore() { return qualityScore; }
    public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }

    public int getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(int communicationScore) { this.communicationScore = communicationScore; }

    public BigDecimal getOverallRating() { return overallRating; }
    public void setOverallRating(BigDecimal overallRating) { this.overallRating = overallRating; }

    public String getEvaluationNotes() { return evaluationNotes; }
    public void setEvaluationNotes(String evaluationNotes) { this.evaluationNotes = evaluationNotes; }

    public Instant getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Instant evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
