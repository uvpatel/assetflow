package com.assetflow.shared.domain;

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
@Table(name = "purchase_order_items")
public class PurchaseOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "purchase_order_id", nullable = false)
    private UUID purchaseOrderId;

    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "quantity_ordered", nullable = false)
    private int quantityOrdered;

    @Column(name = "quantity_received", nullable = false)
    private int quantityReceived = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public PurchaseOrderItem() {
    }

    public PurchaseOrderItem(UUID organizationId, UUID purchaseOrderId, String itemDescription, UUID categoryId, BigDecimal unitPrice, int quantityOrdered) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.purchaseOrderId = Objects.requireNonNull(purchaseOrderId, "Purchase Order ID cannot be null");
        this.itemDescription = Objects.requireNonNull(itemDescription, "Item description cannot be null");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        if (quantityOrdered <= 0) {
            throw new IllegalArgumentException("Quantity ordered must be greater than zero");
        }
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.quantityOrdered = quantityOrdered;
        this.quantityReceived = 0;
        this.createdAt = Instant.now();
    }

    public void receiveQuantity(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Received quantity must be greater than zero");
        }
        if (this.quantityReceived + qty > this.quantityOrdered) {
            throw new IllegalStateException("Total received quantity cannot exceed quantity ordered");
        }
        this.quantityReceived += qty;
    }

    public boolean isFullyReceived() {
        return this.quantityReceived >= this.quantityOrdered;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantityOrdered));
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getPurchaseOrderId() { return purchaseOrderId; }
    public String getItemDescription() { return itemDescription; }
    public UUID getCategoryId() { return categoryId; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantityOrdered() { return quantityOrdered; }
    public int getQuantityReceived() { return quantityReceived; }
    public Instant getCreatedAt() { return createdAt; }
}
