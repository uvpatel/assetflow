package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeatureFlagRepository {
    FeatureFlag save(FeatureFlag flag);
    Optional<FeatureFlag> findByOrganizationIdAndFlagKey(UUID organizationId, String flagKey);
    List<FeatureFlag> findByOrganizationId(UUID organizationId);
}
