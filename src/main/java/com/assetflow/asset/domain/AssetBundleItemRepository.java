package com.assetflow.asset.domain;

import java.util.List;
import java.util.UUID;

public interface AssetBundleItemRepository {
    AssetBundleItem save(AssetBundleItem item);
    List<AssetBundleItem> findByOrganizationIdAndParentAssetId(UUID organizationId, UUID parentAssetId);
    void deleteById(UUID id);
}
