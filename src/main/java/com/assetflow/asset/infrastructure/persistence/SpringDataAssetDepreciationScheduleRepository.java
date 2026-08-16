package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAssetDepreciationScheduleRepository extends JpaRepository<AssetDepreciationScheduleJpaEntity, UUID> {
    Optional<AssetDepreciationScheduleJpaEntity> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
    List<AssetDepreciationScheduleJpaEntity> findByOrganizationId(UUID organizationId);
}
