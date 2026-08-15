package com.assetflow.asset.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetWarrantyRepository {
    AssetWarranty save(AssetWarranty warranty);
    Optional<AssetWarranty> findById(UUID id);
    List<AssetWarranty> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
    List<AssetWarranty> findExpiringWarranties(UUID organizationId, Instant thresholdDate);
}
