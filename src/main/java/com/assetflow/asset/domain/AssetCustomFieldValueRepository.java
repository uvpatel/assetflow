package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetCustomFieldValueRepository {
    AssetCustomFieldValue save(AssetCustomFieldValue value);
    List<AssetCustomFieldValue> saveAll(Iterable<AssetCustomFieldValue> values);
    Optional<AssetCustomFieldValue> findById(UUID id);
    List<AssetCustomFieldValue> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
    Optional<AssetCustomFieldValue> findByAssetIdAndFieldDefinitionId(UUID assetId, UUID fieldDefinitionId);
}
