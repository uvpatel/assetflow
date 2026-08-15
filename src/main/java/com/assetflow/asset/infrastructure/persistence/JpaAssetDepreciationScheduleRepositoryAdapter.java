package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetDepreciationScheduleRepositoryAdapter implements AssetDepreciationScheduleRepository {

    private final SpringDataAssetDepreciationScheduleRepository repository;

    public JpaAssetDepreciationScheduleRepositoryAdapter(SpringDataAssetDepreciationScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetDepreciationSchedule save(AssetDepreciationSchedule schedule) {
        AssetDepreciationScheduleJpaEntity entity = toEntity(schedule);
        AssetDepreciationScheduleJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AssetDepreciationSchedule> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId).map(this::toDomain);
    }

    private AssetDepreciationScheduleJpaEntity toEntity(AssetDepreciationSchedule domain) {
        AssetDepreciationScheduleJpaEntity entity = new AssetDepreciationScheduleJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setDepreciationMethod(domain.getDepreciationMethod().name());
        entity.setInitialCost(domain.getInitialCost());
        entity.setSalvageValue(domain.getSalvageValue());
        entity.setUsefulLifeMonths(domain.getUsefulLifeMonths());
        entity.setCurrentBookValue(domain.getCurrentBookValue());
        entity.setAccumulatedDepreciation(domain.getAccumulatedDepreciation());
        entity.setLastCalculatedAt(domain.getLastCalculatedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private AssetDepreciationSchedule toDomain(AssetDepreciationScheduleJpaEntity entity) {
        return new AssetDepreciationSchedule(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                AssetDepreciationSchedule.DepreciationMethod.valueOf(entity.getDepreciationMethod()),
                entity.getInitialCost(),
                entity.getSalvageValue(),
                entity.getUsefulLifeMonths(),
                entity.getCurrentBookValue(),
                entity.getAccumulatedDepreciation(),
                entity.getLastCalculatedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
