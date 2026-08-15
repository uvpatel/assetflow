package com.assetflow.asset.infrastructure;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAssetRepositoryAdapter implements AssetRepository {

    private final SpringDataAssetRepository repository;

    public JpaAssetRepositoryAdapter(SpringDataAssetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Asset save(Asset asset) {
        return repository.save(asset);
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Asset> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public Optional<Asset> findByOrganizationIdAndAssetTag(UUID organizationId, String assetTag) {
        return repository.findByOrganizationIdAndAssetTag(organizationId, assetTag);
    }

    @Override
    public boolean existsByOrganizationIdAndAssetTag(UUID organizationId, String assetTag) {
        return repository.existsByOrganizationIdAndAssetTag(organizationId, assetTag);
    }
}
