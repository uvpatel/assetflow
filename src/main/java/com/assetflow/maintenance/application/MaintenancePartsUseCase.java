package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenancePartItem;
import com.assetflow.maintenance.domain.MaintenancePartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenancePartsUseCase {

    private static final Logger log = LoggerFactory.getLogger(MaintenancePartsUseCase.class);

    private final MaintenancePartItemRepository repository;

    public MaintenancePartsUseCase(MaintenancePartItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MaintenancePartItem addPartToMaintenance(
            UUID organizationId,
            UUID maintenanceId,
            UUID sparePartId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost) {
        MaintenancePartItem partItem = MaintenancePartItem.create(
                organizationId,
                maintenanceId,
                sparePartId,
                partName,
                partNumber,
                quantityUsed,
                unitCost
        );

        if (sparePartId != null) {
            partItem.markInventoryDeducted();
            log.info("Deducted {} units of spare part {} from inventory for maintenance {}", quantityUsed, sparePartId, maintenanceId);
        }

        return repository.save(partItem);
    }

    @Transactional
    public MaintenancePartItem addPartToMaintenance(
            UUID organizationId,
            UUID maintenanceId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost) {
        return addPartToMaintenance(organizationId, maintenanceId, null, partName, partNumber, quantityUsed, unitCost);
    }

    @Transactional(readOnly = true)
    public List<MaintenancePartItem> getPartsForMaintenance(UUID organizationId, UUID maintenanceId) {
        return repository.findByOrganizationIdAndMaintenanceId(organizationId, maintenanceId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPartsCost(UUID organizationId, UUID maintenanceId) {
        return repository.findByOrganizationIdAndMaintenanceId(organizationId, maintenanceId)
                .stream()
                .map(MaintenancePartItem::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
