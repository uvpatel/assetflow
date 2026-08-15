package com.assetflow.financial.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAssetTaxWriteOffRepository extends JpaRepository<AssetTaxWriteOffJpaEntity, UUID> {
    List<AssetTaxWriteOffJpaEntity> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
