package com.assetflow.audit.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_items")
public class AuditItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "audit_cycle_id", nullable = false)
    private UUID auditCycleId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "verified_condition")
    private String verifiedCondition;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus = "PENDING";

    private String notes;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    public AuditItem() {
    }

    public AuditItem(UUID auditCycleId, UUID assetId) {
        this.auditCycleId = auditCycleId;
        this.assetId = assetId;
        this.verificationStatus = "PENDING";
    }

    public void verify(String verifiedCondition, String verificationStatus, String notes) {
        this.verifiedCondition = verifiedCondition;
        this.verificationStatus = verificationStatus;
        this.notes = notes;
        this.verifiedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuditCycleId() {
        return auditCycleId;
    }

    public void setAuditCycleId(UUID auditCycleId) {
        this.auditCycleId = auditCycleId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public String getVerifiedCondition() {
        return verifiedCondition;
    }

    public void setVerifiedCondition(String verifiedCondition) {
        this.verifiedCondition = verifiedCondition;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
