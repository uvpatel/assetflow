package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAssetWarrantyRepository extends JpaRepository<AssetWarrantyJpaEntity, UUID> {
    List<AssetWarrantyJpaEntity> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);

    @Query("SELECT w FROM AssetWarrantyJpaEntity w WHERE w.organizationId = :organizationId AND w.endDate <= :thresholdDate")
    List<AssetWarrantyJpaEntity> findExpiringWarranties(UUID organizationId, Instant thresholdDate);
}
