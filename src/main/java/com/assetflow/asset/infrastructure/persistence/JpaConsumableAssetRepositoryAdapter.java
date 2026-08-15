package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.ConsumableAsset;
import com.assetflow.asset.domain.ConsumableAssetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaConsumableAssetRepositoryAdapter implements ConsumableAssetRepository {

    private final SpringDataConsumableAssetRepository repository;

    public JpaConsumableAssetRepositoryAdapter(SpringDataConsumableAssetRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConsumableAsset save(ConsumableAsset asset) {
        ConsumableAssetJpaEntity entity = toEntity(asset);
        ConsumableAssetJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ConsumableAsset> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ConsumableAsset> findByOrganizationIdAndSku(UUID organizationId, String sku) {
        return repository.findByOrganizationIdAndSku(organizationId, sku).map(this::toDomain);
    }

    @Override
    public List<ConsumableAsset> findLowStockAssets(UUID organizationId) {
        return repository.findLowStockAssets(organizationId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ConsumableAsset> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private ConsumableAssetJpaEntity toEntity(ConsumableAsset domain) {
        ConsumableAssetJpaEntity entity = new ConsumableAssetJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setSku(domain.getSku());
        entity.setCategoryId(domain.getCategoryId());
        entity.setQuantityOnHand(domain.getQuantityOnHand());
        entity.setMinimumThreshold(domain.getMinimumThreshold());
        entity.setUnitOfMeasure(domain.getUnitOfMeasure());
        entity.setUnitCost(domain.getUnitCost());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private ConsumableAsset toDomain(ConsumableAssetJpaEntity entity) {
        return new ConsumableAsset(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getSku(),
                entity.getCategoryId(),
                entity.getQuantityOnHand(),
                entity.getMinimumThreshold(),
                entity.getUnitOfMeasure(),
                entity.getUnitCost(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
