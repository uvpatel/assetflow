package com.assetflow.allocation.domain;

import java.time.Instant;
import java.util.UUID;

public class TemporaryAssetCheckout {
    private final UUID id;
    private final UUID organizationId;
    private final UUID assetId;
    private final UUID borrowerEmployeeId;
    private final Instant checkoutTime;
    private final Instant expectedReturnTime;
    private Instant actualReturnTime;
    private final String checkoutConditionNotes;
    private String returnConditionNotes;
    private CheckoutStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum CheckoutStatus {
        CHECKED_OUT,
        RETURNED,
        OVERDUE
    }

    public TemporaryAssetCheckout(
            UUID id,
            UUID organizationId,
            UUID assetId,
            UUID borrowerEmployeeId,
            Instant checkoutTime,
            Instant expectedReturnTime,
            Instant actualReturnTime,
            String checkoutConditionNotes,
            String returnConditionNotes,
            CheckoutStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (expectedReturnTime.isBefore(checkoutTime)) {
            throw new IllegalArgumentException("Expected return time cannot be before checkout time");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.borrowerEmployeeId = borrowerEmployeeId;
        this.checkoutTime = checkoutTime;
        this.expectedReturnTime = expectedReturnTime;
        this.actualReturnTime = actualReturnTime;
        this.checkoutConditionNotes = checkoutConditionNotes;
        this.returnConditionNotes = returnConditionNotes;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TemporaryAssetCheckout checkout(
            UUID organizationId,
            UUID assetId,
            UUID borrowerEmployeeId,
            Instant expectedReturnTime,
            String checkoutConditionNotes) {
        Instant now = Instant.now();
        return new TemporaryAssetCheckout(
                UUID.randomUUID(),
                organizationId,
                assetId,
                borrowerEmployeeId,
                now,
                expectedReturnTime,
                null,
                checkoutConditionNotes,
                null,
                CheckoutStatus.CHECKED_OUT,
                now,
                now
        );
    }

    public void returnAsset(String returnConditionNotes) {
        if (this.status == CheckoutStatus.RETURNED) {
            throw new IllegalStateException("Asset has already been returned");
        }
        Instant now = Instant.now();
        this.actualReturnTime = now;
        this.returnConditionNotes = returnConditionNotes;
        this.status = CheckoutStatus.RETURNED;
        this.updatedAt = now;
    }

    public void checkOverdue(Instant now) {
        if (this.status == CheckoutStatus.CHECKED_OUT && now.isAfter(expectedReturnTime)) {
            this.status = CheckoutStatus.OVERDUE;
            this.updatedAt = now;
        }
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getAssetId() { return assetId; }
    public UUID getBorrowerEmployeeId() { return borrowerEmployeeId; }
    public Instant getCheckoutTime() { return checkoutTime; }
    public Instant getExpectedReturnTime() { return expectedReturnTime; }
    public Instant getActualReturnTime() { return actualReturnTime; }
    public String getCheckoutConditionNotes() { return checkoutConditionNotes; }
    public String getReturnConditionNotes() { return returnConditionNotes; }
    public CheckoutStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
