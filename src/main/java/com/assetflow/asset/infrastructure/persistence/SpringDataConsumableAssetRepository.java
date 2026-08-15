package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataConsumableAssetRepository extends JpaRepository<ConsumableAssetJpaEntity, UUID> {
    Optional<ConsumableAssetJpaEntity> findByOrganizationIdAndSku(UUID organizationId, String sku);
    List<ConsumableAssetJpaEntity> findByOrganizationId(UUID organizationId);

    @Query("SELECT c FROM ConsumableAssetJpaEntity c WHERE c.organizationId = :organizationId AND c.quantityOnHand <= c.minimumThreshold")
    List<ConsumableAssetJpaEntity> findLowStockAssets(UUID organizationId);
}
