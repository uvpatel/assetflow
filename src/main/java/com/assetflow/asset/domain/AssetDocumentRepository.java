package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetDocumentRepository {
    AssetDocument save(AssetDocument assetDocument);
    Optional<AssetDocument> findById(UUID id);
    List<AssetDocument> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
