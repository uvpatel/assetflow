package com.assetflow.allocation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetAllocationRepository {
    AssetAllocation save(AssetAllocation allocation);
    Optional<AssetAllocation> findById(UUID id);
    Optional<AssetAllocation> findActiveByAssetId(UUID assetId);
    List<AssetAllocation> findByOrganizationId(UUID organizationId);
}
