package com.assetflow.shared.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PurchaseOrder {
    private final UUID id;
    private final UUID organizationId;
    private final UUID vendorId;
    private final String poNumber;
    private Status status;
    private BigDecimal totalAmount;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum Status {
        DRAFT,
        ISSUED,
        FULFILLED,
        CANCELLED
    }

    public PurchaseOrder(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String poNumber,
            Status status,
            BigDecimal totalAmount,
            Instant createdAt,
            Instant updatedAt) {
        if (poNumber == null || poNumber.isBlank()) {
            throw new IllegalArgumentException("PO number cannot be blank");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.vendorId = vendorId;
        this.poNumber = poNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PurchaseOrder create(
            UUID organizationId,
            UUID vendorId,
            String poNumber,
            BigDecimal initialTotal) {
        Instant now = Instant.now();
        return new PurchaseOrder(
                UUID.randomUUID(),
                organizationId,
                vendorId,
                poNumber,
                Status.DRAFT,
                initialTotal != null ? initialTotal : BigDecimal.ZERO,
                now,
                now
        );
    }

    public void issue() {
        if (this.status != Status.DRAFT) {
            throw new IllegalStateException("Only DRAFT purchase orders can be issued");
        }
        this.status = Status.ISSUED;
        this.updatedAt = Instant.now();
    }

    public void fulfill() {
        if (this.status != Status.ISSUED) {
            throw new IllegalStateException("Only ISSUED purchase orders can be fulfilled");
        }
        this.status = Status.FULFILLED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == Status.FULFILLED) {
            throw new IllegalStateException("FULFILLED purchase orders cannot be cancelled");
        }
        this.status = Status.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void updateTotalAmount(BigDecimal newTotal) {
        if (newTotal == null || newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.totalAmount = newTotal;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getVendorId() { return vendorId; }
    public String getPoNumber() { return poNumber; }
    public Status getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
