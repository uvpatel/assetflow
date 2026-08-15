package com.assetflow.asset.domain;

import java.time.Instant;
import java.util.UUID;

public class AssetConditionAssessment {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final UUID assessorEmployeeId;
    private final int conditionScore;
    private final String assessmentNotes;
    private final Instant assessedAt;

    public AssetConditionAssessment(
            UUID id,
            UUID organizationId,
            UUID assetId,
            UUID assessorEmployeeId,
            int conditionScore,
            String assessmentNotes,
            Instant assessedAt) {
        if (conditionScore < 1 || conditionScore > 10) {
            throw new IllegalArgumentException("Condition score must be between 1 and 10");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.assessorEmployeeId = assessorEmployeeId;
        this.conditionScore = conditionScore;
        this.assessmentNotes = assessmentNotes;
        this.assessedAt = assessedAt;
    }

    public static AssetConditionAssessment assess(
            UUID organizationId,
            UUID assetId,
            UUID assessorEmployeeId,
            int conditionScore,
            String assessmentNotes) {
        return new AssetConditionAssessment(
                UUID.randomUUID(),
                organizationId,
                assetId,
                assessorEmployeeId,
                conditionScore,
                assessmentNotes,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public UUID getAssessorEmployeeId() { return assessorEmployeeId; }
    public int getConditionScore() { return conditionScore; }
    public String getAssessmentNotes() { return assessmentNotes; }
    public Instant getAssessedAt() { return assessedAt; }
}
