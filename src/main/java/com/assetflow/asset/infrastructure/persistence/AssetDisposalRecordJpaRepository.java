package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetDisposalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetDisposalRecordJpaRepository extends JpaRepository<AssetDisposalRecord, UUID> {
    Optional<AssetDisposalRecord> findByOrganizationIdAndId(UUID organizationId, UUID id);
    List<AssetDisposalRecord> findByOrganizationId(UUID organizationId);
    List<AssetDisposalRecord> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}
