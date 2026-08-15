package com.assetflow.maintenance.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MaintenancePartItem {
    private final UUID id;
    private final UUID organizationId;
    private final UUID maintenanceId;
    private final UUID sparePartId;
    private final String partName;
    private final String partNumber;
    private final int quantityUsed;
    private final BigDecimal unitCost;
    private final BigDecimal totalCost;
    private boolean inventoryDeducted;
    private final Instant createdAt;

    public MaintenancePartItem(
            UUID id,
            UUID organizationId,
            UUID maintenanceId,
            UUID sparePartId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost,
            BigDecimal totalCost,
            boolean inventoryDeducted,
            Instant createdAt) {
        if (partName == null || partName.isBlank()) {
            throw new IllegalArgumentException("Part name cannot be blank");
        }
        if (quantityUsed <= 0) {
            throw new IllegalArgumentException("Quantity used must be greater than zero");
        }
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit cost cannot be negative");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.maintenanceId = maintenanceId;
        this.sparePartId = sparePartId;
        this.partName = partName;
        this.partNumber = partNumber;
        this.quantityUsed = quantityUsed;
        this.unitCost = unitCost;
        this.totalCost = totalCost != null ? totalCost : unitCost.multiply(BigDecimal.valueOf(quantityUsed));
        this.inventoryDeducted = inventoryDeducted;
        this.createdAt = createdAt;
    }

    public MaintenancePartItem(
            UUID id,
            UUID organizationId,
            UUID maintenanceId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost,
            BigDecimal totalCost,
            Instant createdAt) {
        this(id, organizationId, maintenanceId, null, partName, partNumber, quantityUsed, unitCost, totalCost, false, createdAt);
    }

    public static MaintenancePartItem create(
            UUID organizationId,
            UUID maintenanceId,
            UUID sparePartId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost) {
        BigDecimal total = unitCost.multiply(BigDecimal.valueOf(quantityUsed));
        return new MaintenancePartItem(
                UUID.randomUUID(),
                organizationId,
                maintenanceId,
                sparePartId,
                partName,
                partNumber,
                quantityUsed,
                unitCost,
                total,
                false,
                Instant.now()
        );
    }

    public static MaintenancePartItem create(
            UUID organizationId,
            UUID maintenanceId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost) {
        return create(organizationId, maintenanceId, null, partName, partNumber, quantityUsed, unitCost);
    }

    public void markInventoryDeducted() {
        this.inventoryDeducted = true;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getMaintenanceId() { return maintenanceId; }
    public UUID getSparePartId() { return sparePartId; }
    public String getPartName() { return partName; }
    public String getPartNumber() { return partNumber; }
    public int getQuantityUsed() { return quantityUsed; }
    public BigDecimal getUnitCost() { return unitCost; }
    public BigDecimal getTotalCost() { return totalCost; }
    public boolean isInventoryDeducted() { return inventoryDeducted; }
    public Instant getCreatedAt() { return createdAt; }
}
