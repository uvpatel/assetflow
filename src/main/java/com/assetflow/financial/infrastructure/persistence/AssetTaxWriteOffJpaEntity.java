package com.assetflow.financial.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_tax_writeoffs")
public class AssetTaxWriteOffJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "writeoff_amount", nullable = false)
    private BigDecimal writeoffAmount;

    @Column(name = "remaining_tax_basis", nullable = false)
    private BigDecimal remainingTaxBasis;

    @Column(name = "writeoff_reason")
    private String writeoffReason;

    @Column(name = "status")
    private String status = "APPROVED";

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AssetTaxWriteOffJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getWriteoffAmount() { return writeoffAmount; }
    public void setWriteoffAmount(BigDecimal writeoffAmount) { this.writeoffAmount = writeoffAmount; }

    public BigDecimal getRemainingTaxBasis() { return remainingTaxBasis; }
    public void setRemainingTaxBasis(BigDecimal remainingTaxBasis) { this.remainingTaxBasis = remainingTaxBasis; }

    public String getWriteoffReason() { return writeoffReason; }
    public void setWriteoffReason(String writeoffReason) { this.writeoffReason = writeoffReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UUID approvedBy) { this.approvedBy = approvedBy; }

    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
