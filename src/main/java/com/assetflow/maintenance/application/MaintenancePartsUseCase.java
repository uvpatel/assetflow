package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenancePartItem;
import com.assetflow.maintenance.domain.MaintenancePartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenancePartsUseCase {

    private final MaintenancePartItemRepository repository;

    public MaintenancePartsUseCase(MaintenancePartItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MaintenancePartItem addPartToMaintenance(
            UUID organizationId,
            UUID maintenanceId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost) {
        MaintenancePartItem partItem = MaintenancePartItem.create(
                organizationId,
                maintenanceId,
                partName,
                partNumber,
                quantityUsed,
                unitCost
        );
        return repository.save(partItem);
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
