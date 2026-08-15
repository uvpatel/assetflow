package com.assetflow.financial.domain;

import java.util.List;
import java.util.UUID;

public interface AssetTaxWriteOffRepository {
    AssetTaxWriteOff save(AssetTaxWriteOff writeOff);
    List<AssetTaxWriteOff> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
