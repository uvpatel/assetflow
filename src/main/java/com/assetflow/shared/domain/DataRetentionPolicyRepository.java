package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataRetentionPolicyRepository {
    DataRetentionPolicy save(DataRetentionPolicy policy);
    Optional<DataRetentionPolicy> findById(UUID id);
    Optional<DataRetentionPolicy> findByOrganizationIdAndTargetModule(UUID organizationId, String targetModule);
    List<DataRetentionPolicy> findByOrganizationId(UUID organizationId);
}
