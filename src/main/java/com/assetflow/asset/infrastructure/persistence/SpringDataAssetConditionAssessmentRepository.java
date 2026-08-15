package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAssetConditionAssessmentRepository extends JpaRepository<AssetConditionAssessmentJpaEntity, UUID> {
    List<AssetConditionAssessmentJpaEntity> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
