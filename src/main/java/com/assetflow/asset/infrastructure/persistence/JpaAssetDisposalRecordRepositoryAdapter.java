package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetDisposalRecord;
import com.assetflow.asset.domain.AssetDisposalRecordRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetDisposalRecordRepositoryAdapter implements AssetDisposalRecordRepository {

    private final AssetDisposalRecordJpaRepository jpaRepository;

    public JpaAssetDisposalRecordRepositoryAdapter(AssetDisposalRecordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AssetDisposalRecord save(AssetDisposalRecord record) {
        return jpaRepository.save(record);
    }

    @Override
    public Optional<AssetDisposalRecord> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<AssetDisposalRecord> findByOrganizationIdAndId(UUID organizationId, UUID id) {
        return jpaRepository.findByOrganizationIdAndId(organizationId, id);
    }

    @Override
    public List<AssetDisposalRecord> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findByOrganizationId(organizationId);
    }

    @Override
    public List<AssetDisposalRecord> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return jpaRepository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
