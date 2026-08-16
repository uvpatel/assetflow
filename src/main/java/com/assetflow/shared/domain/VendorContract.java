package com.assetflow.shared.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class VendorContract {

    public enum ContractStatus {
        ACTIVE,
        EXPIRED,
        RENEWED,
        TERMINATED
    }

    private final UUID id;
    private final UUID organizationId;
    private final UUID vendorId;
    private final String contractNumber;
    private final String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal contractValue;
    private boolean isAutoRenew;
    private int noticePeriodDays;
    private ContractStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public VendorContract(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew,
            int noticePeriodDays,
            ContractStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (contractNumber == null || contractNumber.isBlank()) {
            throw new IllegalArgumentException("Contract number cannot be blank");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Contract end date must be on or after start date");
        }
        if (contractValue == null || contractValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Contract value cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.vendorId = vendorId;
        this.contractNumber = contractNumber;
        this.contractType = contractType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractValue = contractValue;
        this.isAutoRenew = isAutoRenew;
        this.noticePeriodDays = noticePeriodDays > 0 ? noticePeriodDays : 30;
        this.status = status != null ? status : ContractStatus.ACTIVE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public VendorContract(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew,
            Instant createdAt,
            Instant updatedAt) {
        this(id, organizationId, vendorId, contractNumber, contractType, startDate, endDate, contractValue, isAutoRenew, 30, ContractStatus.ACTIVE, createdAt, updatedAt);
    }

    public static VendorContract create(
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew,
            int noticePeriodDays) {
        Instant now = Instant.now();
        return new VendorContract(
                UUID.randomUUID(),
                organizationId,
                vendorId,
                contractNumber,
                contractType,
                startDate,
                endDate,
                contractValue,
                isAutoRenew,
                noticePeriodDays > 0 ? noticePeriodDays : 30,
                ContractStatus.ACTIVE,
                now,
                now
        );
    }

    public static VendorContract create(
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew) {
        return create(organizationId, vendorId, contractNumber, contractType, startDate, endDate, contractValue, isAutoRenew, 30);
    }

    public void renewContract(LocalDate newEndDate, BigDecimal newContractValue) {
        if (newEndDate.isBefore(this.endDate)) {
            throw new IllegalArgumentException("Renewed end date cannot be earlier than current end date");
        }
        this.endDate = newEndDate;
        if (newContractValue != null && newContractValue.compareTo(BigDecimal.ZERO) >= 0) {
            this.contractValue = newContractValue;
        }
        this.status = ContractStatus.RENEWED;
        this.updatedAt = Instant.now();
    }

    public void terminate() {
        this.status = ContractStatus.TERMINATED;
        this.updatedAt = Instant.now();
    }

    public boolean shouldSendRenewalNotice(LocalDate referenceDate) {
        if (this.status != ContractStatus.ACTIVE) return false;
        if (isAutoRenew) return false;
        LocalDate noticeDate = endDate.minusDays(noticePeriodDays);
        return !referenceDate.isBefore(noticeDate) && !referenceDate.isAfter(endDate);
    }

    public boolean isExpiringWithinDays(LocalDate referenceDate, int daysThreshold) {
        if (referenceDate.isAfter(endDate)) return false;
        return !referenceDate.plusDays(daysThreshold).isBefore(endDate);
    }

    public boolean isExpired(LocalDate referenceDate) {
        return referenceDate.isAfter(endDate);
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getVendorId() { return vendorId; }
    public String getContractNumber() { return contractNumber; }
    public String getContractType() { return contractType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getContractValue() { return contractValue; }
    public boolean isAutoRenew() { return isAutoRenew; }
    public int getNoticePeriodDays() { return noticePeriodDays; }
    public ContractStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
