package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetBundleItem;
import com.assetflow.asset.domain.AssetBundleItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaAssetBundleItemRepositoryAdapter implements AssetBundleItemRepository {

    private final SpringDataAssetBundleItemRepository repository;

    public JpaAssetBundleItemRepositoryAdapter(SpringDataAssetBundleItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetBundleItem save(AssetBundleItem item) {
        AssetBundleItemJpaEntity entity = toEntity(item);
        AssetBundleItemJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AssetBundleItem> findByOrganizationIdAndParentAssetId(UUID organizationId, UUID parentAssetId) {
        return repository.findByOrganizationIdAndParentAssetId(organizationId, parentAssetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private AssetBundleItemJpaEntity toEntity(AssetBundleItem domain) {
        AssetBundleItemJpaEntity entity = new AssetBundleItemJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setParentAssetId(domain.getParentAssetId());
        entity.setChildAssetId(domain.getChildAssetId());
        entity.setRelationshipType(domain.getRelationshipType().name());
        entity.setQuantity(domain.getQuantity());
        entity.setNotes(domain.getNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private AssetBundleItem toDomain(AssetBundleItemJpaEntity entity) {
        return new AssetBundleItem(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getParentAssetId(),
                entity.getChildAssetId(),
                AssetBundleItem.RelationshipType.valueOf(entity.getRelationshipType()),
                entity.getQuantity(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }
}
