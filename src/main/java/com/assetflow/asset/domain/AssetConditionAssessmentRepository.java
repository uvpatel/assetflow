package com.assetflow.asset.domain;

import java.util.List;
import java.util.UUID;

public interface AssetConditionAssessmentRepository {
    AssetConditionAssessment save(AssetConditionAssessment assessment);
    List<AssetConditionAssessment> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
