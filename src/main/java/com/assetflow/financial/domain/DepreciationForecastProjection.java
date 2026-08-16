package com.assetflow.financial.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "asset_depreciation_forecast_projections")
public class DepreciationForecastProjection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "projected_depreciation", nullable = false)
    private BigDecimal projectedDepreciation;

    @Column(name = "projected_ending_book_value", nullable = false)
    private BigDecimal projectedEndingBookValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public DepreciationForecastProjection() {}

    public DepreciationForecastProjection(
            UUID organizationId,
            int fiscalYear,
            BigDecimal projectedDepreciation,
            BigDecimal projectedEndingBookValue) {
        if (fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("Invalid fiscal year");
        }
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.fiscalYear = fiscalYear;
        this.projectedDepreciation = projectedDepreciation != null ? projectedDepreciation : BigDecimal.ZERO;
        this.projectedEndingBookValue = projectedEndingBookValue != null ? projectedEndingBookValue : BigDecimal.ZERO;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public int getFiscalYear() { return fiscalYear; }
    public BigDecimal getProjectedDepreciation() { return projectedDepreciation; }
    public BigDecimal getProjectedEndingBookValue() { return projectedEndingBookValue; }
    public Instant getCreatedAt() { return createdAt; }
}
