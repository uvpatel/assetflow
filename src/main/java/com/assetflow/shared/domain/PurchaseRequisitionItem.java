package com.assetflow.shared.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "purchase_requisition_items")
public class PurchaseRequisitionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "requisition_id", nullable = false)
    private UUID requisitionId;

    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "estimated_unit_price", nullable = false)
    private BigDecimal estimatedUnitPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private int quantity = 1;

    public PurchaseRequisitionItem() {
    }

    public PurchaseRequisitionItem(
            UUID organizationId,
            UUID requisitionId,
            String itemDescription,
            UUID categoryId,
            BigDecimal estimatedUnitPrice,
            int quantity) {
        if (itemDescription == null || itemDescription.isBlank()) {
            throw new IllegalArgumentException("Item description cannot be blank");
        }
        if (estimatedUnitPrice == null || estimatedUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estimated unit price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId cannot be null");
        this.requisitionId = Objects.requireNonNull(requisitionId, "requisitionId cannot be null");
        this.itemDescription = itemDescription;
        this.categoryId = categoryId;
        this.estimatedUnitPrice = estimatedUnitPrice;
        this.quantity = quantity;
    }

    public BigDecimal calculateTotal() {
        return estimatedUnitPrice.multiply(BigDecimal.valueOf(quantity));
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

    public UUID getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(UUID requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getEstimatedUnitPrice() {
        return estimatedUnitPrice;
    }

    public void setEstimatedUnitPrice(BigDecimal estimatedUnitPrice) {
        this.estimatedUnitPrice = estimatedUnitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
