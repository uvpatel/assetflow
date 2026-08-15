package com.assetflow.asset.infrastructure;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetCategory;
import com.assetflow.asset.domain.AssetCategoryRepository;
import com.assetflow.asset.domain.AssetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {
    List<AssetCategory> findByOrganizationId(UUID organizationId);
}

interface SpringDataAssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByOrganizationId(UUID organizationId);
    Optional<Asset> findByOrganizationIdAndAssetTag(UUID organizationId, String assetTag);
    boolean existsByOrganizationIdAndAssetTag(UUID organizationId, String assetTag);
}

@Repository
public class JpaAssetCategoryRepositoryAdapter implements AssetCategoryRepository {

    private final SpringDataAssetCategoryRepository repository;

    public JpaAssetCategoryRepositoryAdapter(SpringDataAssetCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetCategory save(AssetCategory category) {
        return repository.save(category);
    }

    @Override
    public Optional<AssetCategory> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AssetCategory> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}

