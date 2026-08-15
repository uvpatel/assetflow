package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetBundleItem;
import com.assetflow.asset.domain.AssetBundleItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssetBundleUseCase {

    private final AssetBundleItemRepository repository;

    public AssetBundleUseCase(AssetBundleItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AssetBundleItem addBundleItem(
            UUID organizationId,
            UUID parentAssetId,
            UUID childAssetId,
            String relationshipTypeStr,
            int quantity,
            String notes) {
        AssetBundleItem.RelationshipType type = AssetBundleItem.RelationshipType.valueOf(relationshipTypeStr.toUpperCase());
        AssetBundleItem item = AssetBundleItem.create(
                organizationId,
                parentAssetId,
                childAssetId,
                type,
                quantity,
                notes
        );
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    public List<AssetBundleItem> getBundleItems(UUID organizationId, UUID parentAssetId) {
        return repository.findByOrganizationIdAndParentAssetId(organizationId, parentAssetId);
    }

    @Transactional
    public void removeBundleItem(UUID itemId) {
        repository.deleteById(itemId);
    }
}
