package com.assetflow.maintenance.infrastructure.persistence;

import com.assetflow.maintenance.domain.MaintenancePartItem;
import com.assetflow.maintenance.domain.MaintenancePartItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaMaintenancePartItemRepositoryAdapter implements MaintenancePartItemRepository {

    private final SpringDataMaintenancePartItemRepository repository;

    public JpaMaintenancePartItemRepositoryAdapter(SpringDataMaintenancePartItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public MaintenancePartItem save(MaintenancePartItem partItem) {
        MaintenancePartItemJpaEntity entity = toEntity(partItem);
        MaintenancePartItemJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<MaintenancePartItem> findByOrganizationIdAndMaintenanceId(UUID organizationId, UUID maintenanceId) {
        return repository.findByOrganizationIdAndMaintenanceId(organizationId, maintenanceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private MaintenancePartItemJpaEntity toEntity(MaintenancePartItem domain) {
        MaintenancePartItemJpaEntity entity = new MaintenancePartItemJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setMaintenanceId(domain.getMaintenanceId());
        entity.setPartName(domain.getPartName());
        entity.setPartNumber(domain.getPartNumber());
        entity.setQuantityUsed(domain.getQuantityUsed());
        entity.setUnitCost(domain.getUnitCost());
        entity.setTotalCost(domain.getTotalCost());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private MaintenancePartItem toDomain(MaintenancePartItemJpaEntity entity) {
        return new MaintenancePartItem(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getMaintenanceId(),
                entity.getPartName(),
                entity.getPartNumber(),
                entity.getQuantityUsed(),
                entity.getUnitCost(),
                entity.getTotalCost(),
                entity.getCreatedAt()
        );
    }
}
