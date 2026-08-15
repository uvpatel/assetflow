package com.assetflow.maintenance.infrastructure.persistence;

import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import com.assetflow.maintenance.domain.AssetMaintenanceScheduleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetMaintenanceScheduleRepositoryAdapter implements AssetMaintenanceScheduleRepository {

    private final SpringDataAssetMaintenanceScheduleRepository repository;

    public JpaAssetMaintenanceScheduleRepositoryAdapter(SpringDataAssetMaintenanceScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetMaintenanceSchedule save(AssetMaintenanceSchedule schedule) {
        AssetMaintenanceScheduleJpaEntity entity = toEntity(schedule);
        AssetMaintenanceScheduleJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AssetMaintenanceSchedule> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AssetMaintenanceSchedule> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AssetMaintenanceScheduleJpaEntity toEntity(AssetMaintenanceSchedule domain) {
        AssetMaintenanceScheduleJpaEntity entity = new AssetMaintenanceScheduleJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setTitle(domain.getTitle());
        entity.setFrequencyDays(domain.getFrequencyDays());
        entity.setNextDueDate(domain.getNextDueDate());
        entity.setLastPerformedAt(domain.getLastPerformedAt());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private AssetMaintenanceSchedule toDomain(AssetMaintenanceScheduleJpaEntity entity) {
        return new AssetMaintenanceSchedule(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getTitle(),
                entity.getFrequencyDays(),
                entity.getNextDueDate(),
                entity.getLastPerformedAt(),
                AssetMaintenanceSchedule.ScheduleStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
