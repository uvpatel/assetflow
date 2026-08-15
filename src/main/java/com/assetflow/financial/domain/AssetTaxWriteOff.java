package com.assetflow.financial.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AssetTaxWriteOff {

    public enum WriteOffStatus {
        PENDING_APPROVAL,
        APPROVED,
        REJECTED
    }

    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("10000.00");

    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final int fiscalYear;
    private final BigDecimal writeoffAmount;
    private final BigDecimal remainingTaxBasis;
    private final String writeoffReason;
    private WriteOffStatus status;
    private UUID approvedBy;
    private Instant approvedAt;
    private final Instant createdAt;

    public AssetTaxWriteOff(
            UUID id,
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal writeoffAmount,
            BigDecimal remainingTaxBasis,
            String writeoffReason,
            WriteOffStatus status,
            UUID approvedBy,
            Instant approvedAt,
            Instant createdAt) {
        if (fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("Fiscal year must be a valid 4-digit year");
        }
        if (writeoffAmount == null || writeoffAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Write-off amount must be greater than zero");
        }
        if (remainingTaxBasis == null || remainingTaxBasis.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Remaining tax basis cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.fiscalYear = fiscalYear;
        this.writeoffAmount = writeoffAmount;
        this.remainingTaxBasis = remainingTaxBasis;
        this.writeoffReason = writeoffReason;
        this.status = status != null ? status : WriteOffStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
    }

    public AssetTaxWriteOff(
            UUID id,
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal writeoffAmount,
            BigDecimal remainingTaxBasis,
            String writeoffReason,
            Instant createdAt) {
        this(id, organizationId, assetId, fiscalYear, writeoffAmount, remainingTaxBasis, writeoffReason, WriteOffStatus.APPROVED, null, null, createdAt);
    }

    public static AssetTaxWriteOff executeWriteOff(
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal currentTaxBasis,
            BigDecimal writeoffAmount,
            String writeoffReason) {
        if (currentTaxBasis == null || writeoffAmount.compareTo(currentTaxBasis) > 0) {
            throw new IllegalArgumentException("Write-off amount cannot exceed current tax basis");
        }
        BigDecimal newRemainingBasis = currentTaxBasis.subtract(writeoffAmount);

        boolean requiresApproval = writeoffAmount.compareTo(APPROVAL_THRESHOLD) >= 0;
        WriteOffStatus initialStatus = requiresApproval ? WriteOffStatus.PENDING_APPROVAL : WriteOffStatus.APPROVED;

        return new AssetTaxWriteOff(
                UUID.randomUUID(),
                organizationId,
                assetId,
                fiscalYear,
                writeoffAmount,
                newRemainingBasis,
                writeoffReason,
                initialStatus,
                null,
                requiresApproval ? null : Instant.now(),
                Instant.now()
        );
    }

    public void approve(UUID approverUserId) {
        if (status != WriteOffStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only PENDING_APPROVAL write-offs can be approved");
        }
        this.approvedBy = Objects.requireNonNull(approverUserId, "Approver user ID cannot be null");
        this.approvedAt = Instant.now();
        this.status = WriteOffStatus.APPROVED;
    }

    public void reject(UUID approverUserId) {
        if (status != WriteOffStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only PENDING_APPROVAL write-offs can be rejected");
        }
        this.approvedBy = Objects.requireNonNull(approverUserId, "Approver user ID cannot be null");
        this.approvedAt = Instant.now();
        this.status = WriteOffStatus.REJECTED;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public int getFiscalYear() { return fiscalYear; }
    public BigDecimal getWriteoffAmount() { return writeoffAmount; }
    public BigDecimal getRemainingTaxBasis() { return remainingTaxBasis; }
    public String getWriteoffReason() { return writeoffReason; }
    public WriteOffStatus getStatus() { return status; }
    public UUID getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
