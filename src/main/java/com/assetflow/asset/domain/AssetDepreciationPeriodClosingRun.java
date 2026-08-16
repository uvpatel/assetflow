package com.assetflow.asset.domain;

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
@Table(name = "asset_depreciation_period_closing_runs")
public class AssetDepreciationPeriodClosingRun extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "period_month", nullable = false)
    private int periodMonth;

    @Column(name = "processed_schedules_count", nullable = false)
    private int processedSchedulesCount;

    @Column(name = "total_depreciation_amount", nullable = false)
    private BigDecimal totalDepreciationAmount;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt = Instant.now();

    @Column(name = "status", nullable = false)
    private String status = "COMPLETED";

    public AssetDepreciationPeriodClosingRun() {}

    public AssetDepreciationPeriodClosingRun(
            UUID organizationId,
            int periodYear,
            int periodMonth,
            int processedSchedulesCount,
            BigDecimal totalDepreciationAmount) {
        if (periodMonth < 1 || periodMonth > 12) {
            throw new IllegalArgumentException("Invalid period month (must be 1-12)");
        }
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.processedSchedulesCount = processedSchedulesCount;
        this.totalDepreciationAmount = totalDepreciationAmount != null ? totalDepreciationAmount : BigDecimal.ZERO;
        this.executedAt = Instant.now();
        this.status = "COMPLETED";
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public int getPeriodYear() { return periodYear; }
    public int getPeriodMonth() { return periodMonth; }
    public int getProcessedSchedulesCount() { return processedSchedulesCount; }
    public BigDecimal getTotalDepreciationAmount() { return totalDepreciationAmount; }
    public Instant getExecutedAt() { return executedAt; }
    public String getStatus() { return status; }
}
