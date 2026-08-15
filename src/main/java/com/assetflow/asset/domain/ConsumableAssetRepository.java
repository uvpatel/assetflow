package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumableAssetRepository {
    ConsumableAsset save(ConsumableAsset asset);
    Optional<ConsumableAsset> findById(UUID id);
    Optional<ConsumableAsset> findByOrganizationIdAndSku(UUID organizationId, String sku);
    List<ConsumableAsset> findLowStockAssets(UUID organizationId);
    List<ConsumableAsset> findByOrganizationId(UUID organizationId);
}
