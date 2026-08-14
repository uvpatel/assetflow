package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    Optional<Asset> findById(UUID id);
    List<Asset> findByOrganizationId(UUID organizationId);
    Optional<Asset> findByOrganizationIdAndAssetTag(UUID organizationId, String assetTag);
    boolean existsByOrganizationIdAndAssetTag(UUID organizationId, String assetTag);
}
