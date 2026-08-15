package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.ConsumableBatch;
import com.assetflow.asset.domain.ConsumableBatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaConsumableBatchRepositoryAdapter implements ConsumableBatchRepository {

    private final SpringDataConsumableBatchRepository repository;

    public JpaConsumableBatchRepositoryAdapter(SpringDataConsumableBatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConsumableBatch save(ConsumableBatch batch) {
        ConsumableBatchJpaEntity entity = toEntity(batch);
        ConsumableBatchJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ConsumableBatch> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ConsumableBatch> findByAssetId(UUID assetId) {
        return repository.findByAssetId(assetId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ConsumableBatch> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private ConsumableBatchJpaEntity toEntity(ConsumableBatch domain) {
        ConsumableBatchJpaEntity entity = new ConsumableBatchJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setBatchNumber(domain.getBatchNumber());
        entity.setInitialQuantity(domain.getInitialQuantity());
        entity.setCurrentQuantity(domain.getCurrentQuantity());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private ConsumableBatch toDomain(ConsumableBatchJpaEntity entity) {
        return new ConsumableBatch(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getBatchNumber(),
                entity.getInitialQuantity(),
                entity.getCurrentQuantity(),
                entity.getExpiryDate(),
                entity.getCreatedAt()
        );
    }
}
