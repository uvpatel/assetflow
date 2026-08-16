package com.assetflow.asset.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "asset_disposal_records")
public class AssetDisposalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "disposal_type", nullable = false)
    private DisposalType disposalType;

    @Column(name = "disposal_date", nullable = false)
    private Instant disposalDate;

    @Column(name = "proceeds_amount", nullable = false)
    private BigDecimal proceedsAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 8)
    private String currency = "USD";

    @Column(name = "disposal_reason", nullable = false, length = 1000)
    private String disposalReason;

    @Column(name = "authorized_by_user_id")
    private UUID authorizedByUserId;

    @Column(name = "certificate_reference", length = 128)
    private String certificateReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisposalStatus status = DisposalStatus.DRAFT;

    public enum DisposalType {
        SALE,
        SCRAP,
        DONATION,
        RECYCLE,
        LOST_WRITE_OFF
    }

    public enum DisposalStatus {
        DRAFT,
        APPROVED,
        EXECUTED,
        CANCELLED
    }

    public AssetDisposalRecord() {
    }

    public AssetDisposalRecord(
            UUID organizationId,
            UUID assetId,
            DisposalType disposalType,
            Instant disposalDate,
            BigDecimal proceedsAmount,
            String currency,
            String disposalReason,
            UUID authorizedByUserId,
            String certificateReference,
            DisposalStatus status) {
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId cannot be null");
        this.assetId = Objects.requireNonNull(assetId, "assetId cannot be null");
        this.disposalType = Objects.requireNonNull(disposalType, "disposalType cannot be null");
        this.disposalDate = disposalDate != null ? disposalDate : Instant.now();
        this.proceedsAmount = proceedsAmount != null ? proceedsAmount : BigDecimal.ZERO;
        this.currency = currency != null && !currency.isBlank() ? currency : "USD";
        this.disposalReason = Objects.requireNonNull(disposalReason, "disposalReason cannot be null");
        this.authorizedByUserId = authorizedByUserId;
        this.certificateReference = certificateReference;
        this.status = status != null ? status : DisposalStatus.DRAFT;
    }

    public static AssetDisposalRecord initiate(
            UUID organizationId,
            UUID assetId,
            DisposalType disposalType,
            String disposalReason,
            BigDecimal proceedsAmount,
            String currency,
            UUID authorizedByUserId,
            String certificateReference) {
        AssetDisposalRecord record = new AssetDisposalRecord(
                organizationId,
                assetId,
                disposalType,
                Instant.now(),
                proceedsAmount,
                currency,
                disposalReason,
                authorizedByUserId,
                certificateReference,
                DisposalStatus.DRAFT
        );
        record.setId(UUID.randomUUID());
        return record;
    }

    public void approve(UUID authorizedByUserId) {
        if (this.status != DisposalStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT disposal records can be approved");
        }
        this.status = DisposalStatus.APPROVED;
        if (authorizedByUserId != null) {
            this.authorizedByUserId = authorizedByUserId;
        }
    }

    public void execute() {
        if (this.status != DisposalStatus.DRAFT && this.status != DisposalStatus.APPROVED) {
            throw new IllegalStateException("Only DRAFT or APPROVED disposal records can be executed");
        }
        this.status = DisposalStatus.EXECUTED;
        this.disposalDate = Instant.now();
    }

    public void cancel() {
        if (this.status == DisposalStatus.EXECUTED) {
            throw new IllegalStateException("Executed disposal records cannot be cancelled");
        }
        this.status = DisposalStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public DisposalType getDisposalType() {
        return disposalType;
    }

    public void setDisposalType(DisposalType disposalType) {
        this.disposalType = disposalType;
    }

    public Instant getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(Instant disposalDate) {
        this.disposalDate = disposalDate;
    }

    public BigDecimal getProceedsAmount() {
        return proceedsAmount;
    }

    public void setProceedsAmount(BigDecimal proceedsAmount) {
        this.proceedsAmount = proceedsAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDisposalReason() {
        return disposalReason;
    }

    public void setDisposalReason(String disposalReason) {
        this.disposalReason = disposalReason;
    }

    public UUID getAuthorizedByUserId() {
        return authorizedByUserId;
    }

    public void setAuthorizedByUserId(UUID authorizedByUserId) {
        this.authorizedByUserId = authorizedByUserId;
    }

    public String getCertificateReference() {
        return certificateReference;
    }

    public void setCertificateReference(String certificateReference) {
        this.certificateReference = certificateReference;
    }

    public DisposalStatus getStatus() {
        return status;
    }

    public void setStatus(DisposalStatus status) {
        this.status = status;
    }
}
