package com.assetflow.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFeatureFlagRepository extends JpaRepository<FeatureFlagJpaEntity, UUID> {
    Optional<FeatureFlagJpaEntity> findByOrganizationIdAndFlagKey(UUID organizationId, String flagKey);
    List<FeatureFlagJpaEntity> findByOrganizationId(UUID organizationId);
}
