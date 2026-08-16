package com.assetflow.financial.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DepartmentBudget {

    private final UUID id;
    private final UUID organizationId;
    private final UUID departmentId;
    private final int fiscalYear;
    private BigDecimal allocatedAmount;
    private BigDecimal committedAmount;
    private BigDecimal spentAmount;
    private String currency;
    private BudgetStatus status;
    private String notes;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum BudgetStatus {
        ACTIVE,
        FROZEN,
        CLOSED
    }

    public DepartmentBudget(
            UUID id,
            UUID organizationId,
            UUID departmentId,
            int fiscalYear,
            BigDecimal allocatedAmount,
            BigDecimal committedAmount,
            BigDecimal spentAmount,
            String currency,
            BudgetStatus status,
            String notes,
            Instant createdAt,
            Instant updatedAt) {
        if (fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("Fiscal year must be between 2000 and 2100");
        }
        if (allocatedAmount == null || allocatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Allocated amount cannot be negative");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId cannot be null");
        this.departmentId = Objects.requireNonNull(departmentId, "departmentId cannot be null");
        this.fiscalYear = fiscalYear;
        this.allocatedAmount = allocatedAmount;
        this.committedAmount = committedAmount != null ? committedAmount : BigDecimal.ZERO;
        this.spentAmount = spentAmount != null ? spentAmount : BigDecimal.ZERO;
        this.currency = currency != null && !currency.isBlank() ? currency : "USD";
        this.status = status != null ? status : BudgetStatus.ACTIVE;
        this.notes = notes;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static DepartmentBudget create(
            UUID organizationId,
            UUID departmentId,
            int fiscalYear,
            BigDecimal allocatedAmount,
            String currency,
            String notes) {
        Instant now = Instant.now();
        return new DepartmentBudget(
                UUID.randomUUID(),
                organizationId,
                departmentId,
                fiscalYear,
                allocatedAmount,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                currency,
                BudgetStatus.ACTIVE,
                notes,
                now,
                now
        );
    }

    public BigDecimal getRemainingBudget() {
        return allocatedAmount.subtract(committedAmount).subtract(spentAmount);
    }

    public boolean hasSufficientBudget(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return getRemainingBudget().compareTo(amount) >= 0;
    }

    public void commitFunds(BigDecimal amount) {
        if (this.status != BudgetStatus.ACTIVE) {
            throw new IllegalStateException("Cannot commit funds to non-ACTIVE budget");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Commit amount must be greater than zero");
        }
        if (!hasSufficientBudget(amount)) {
            throw new IllegalStateException("Insufficient budget remaining to commit funds");
        }
        this.committedAmount = this.committedAmount.add(amount);
        this.updatedAt = Instant.now();
    }

    public void releaseCommittedFunds(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Release amount must be greater than zero");
        }
        this.committedAmount = this.committedAmount.subtract(amount).max(BigDecimal.ZERO);
        this.updatedAt = Instant.now();
    }

    public void recordSpend(BigDecimal amount, boolean fromCommitted) {
        if (this.status != BudgetStatus.ACTIVE) {
            throw new IllegalStateException("Cannot record spend on non-ACTIVE budget");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Spend amount must be greater than zero");
        }
        if (fromCommitted) {
            this.committedAmount = this.committedAmount.subtract(amount).max(BigDecimal.ZERO);
        } else {
            if (!hasSufficientBudget(amount)) {
                throw new IllegalStateException("Insufficient uncommitted budget to record direct spend");
            }
        }
        this.spentAmount = this.spentAmount.add(amount);
        this.updatedAt = Instant.now();
    }

    public void increaseAllocation(BigDecimal additionalAmount) {
        if (this.status == BudgetStatus.CLOSED) {
            throw new IllegalStateException("Cannot adjust closed budget");
        }
        if (additionalAmount == null || additionalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Additional amount must be positive");
        }
        this.allocatedAmount = this.allocatedAmount.add(additionalAmount);
        this.updatedAt = Instant.now();
    }

    public void freeze() {
        if (this.status == BudgetStatus.CLOSED) {
            throw new IllegalStateException("Closed budgets cannot be frozen");
        }
        this.status = BudgetStatus.FROZEN;
        this.updatedAt = Instant.now();
    }

    public void unfreeze() {
        if (this.status == BudgetStatus.CLOSED) {
            throw new IllegalStateException("Closed budgets cannot be unfrozen");
        }
        this.status = BudgetStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void close() {
        this.status = BudgetStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public BigDecimal getCommittedAmount() {
        return committedAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
