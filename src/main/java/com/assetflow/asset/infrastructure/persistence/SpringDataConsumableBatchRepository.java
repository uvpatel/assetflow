package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataConsumableBatchRepository extends JpaRepository<ConsumableBatchJpaEntity, UUID> {
    List<ConsumableBatchJpaEntity> findByAssetId(UUID assetId);
    List<ConsumableBatchJpaEntity> findByOrganizationId(UUID organizationId);
}
