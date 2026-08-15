package com.assetflow.financial.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetTaxWriteOffRepository {
    AssetTaxWriteOff save(AssetTaxWriteOff writeOff);
    Optional<AssetTaxWriteOff> findById(UUID id);
    List<AssetTaxWriteOff> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
