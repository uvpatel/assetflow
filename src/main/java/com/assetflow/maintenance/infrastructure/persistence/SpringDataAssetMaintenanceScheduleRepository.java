package com.assetflow.maintenance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAssetMaintenanceScheduleRepository extends JpaRepository<AssetMaintenanceScheduleJpaEntity, UUID> {
    List<AssetMaintenanceScheduleJpaEntity> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
