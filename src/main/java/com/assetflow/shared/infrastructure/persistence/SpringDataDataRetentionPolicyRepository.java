package com.assetflow.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataDataRetentionPolicyRepository extends JpaRepository<DataRetentionPolicyJpaEntity, UUID> {
    Optional<DataRetentionPolicyJpaEntity> findByOrganizationIdAndTargetModule(UUID organizationId, String targetModule);
    List<DataRetentionPolicyJpaEntity> findByOrganizationId(UUID organizationId);
}
