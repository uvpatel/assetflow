package com.assetflow.asset.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_condition_assessments")
public class AssetConditionAssessmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "assessor_employee_id", nullable = false)
    private UUID assessorEmployeeId;

    @Column(name = "condition_score", nullable = false)
    private int conditionScore;

    @Column(name = "assessment_notes")
    private String assessmentNotes;

    @Column(name = "assessed_at", nullable = false)
    private Instant assessedAt;

    public AssetConditionAssessmentJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }

    public UUID getAssessorEmployeeId() { return assessorEmployeeId; }
    public void setAssessorEmployeeId(UUID assessorEmployeeId) { this.assessorEmployeeId = assessorEmployeeId; }

    public int getConditionScore() { return conditionScore; }
    public void setConditionScore(int conditionScore) { this.conditionScore = conditionScore; }

    public String getAssessmentNotes() { return assessmentNotes; }
    public void setAssessmentNotes(String assessmentNotes) { this.assessmentNotes = assessmentNotes; }

    public Instant getAssessedAt() { return assessedAt; }
    public void setAssessedAt(Instant assessedAt) { this.assessedAt = assessedAt; }
}
