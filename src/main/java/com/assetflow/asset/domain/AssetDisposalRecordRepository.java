package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetDisposalRecordRepository {
    AssetDisposalRecord save(AssetDisposalRecord record);
    Optional<AssetDisposalRecord> findById(UUID id);
    Optional<AssetDisposalRecord> findByOrganizationIdAndId(UUID organizationId, UUID id);
    List<AssetDisposalRecord> findByOrganizationId(UUID organizationId);
    List<AssetDisposalRecord> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
