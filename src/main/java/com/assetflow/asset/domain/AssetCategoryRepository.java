package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetCategoryRepository {
    AssetCategory save(AssetCategory category);
    Optional<AssetCategory> findById(UUID id);
    List<AssetCategory> findByOrganizationId(UUID organizationId);
}
