package com.assetflow.asset.infrastructure;

import com.assetflow.asset.domain.AssetCustomFieldValue;
import com.assetflow.asset.domain.AssetCustomFieldValueRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAssetCustomFieldValueRepository extends JpaRepository<AssetCustomFieldValue, UUID> {
    List<AssetCustomFieldValue> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
    Optional<AssetCustomFieldValue> findByAssetIdAndFieldDefinitionId(UUID assetId, UUID fieldDefinitionId);
}

@Repository
public class JpaAssetCustomFieldValueRepositoryAdapter implements AssetCustomFieldValueRepository {

    private final SpringDataAssetCustomFieldValueRepository repository;

    public JpaAssetCustomFieldValueRepositoryAdapter(SpringDataAssetCustomFieldValueRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetCustomFieldValue save(AssetCustomFieldValue value) {
        return repository.save(value);
    }

    @Override
    public List<AssetCustomFieldValue> saveAll(Iterable<AssetCustomFieldValue> values) {
        return repository.saveAll(values);
    }

    @Override
    public Optional<AssetCustomFieldValue> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AssetCustomFieldValue> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }

    @Override
    public Optional<AssetCustomFieldValue> findByAssetIdAndFieldDefinitionId(UUID assetId, UUID fieldDefinitionId) {
        return repository.findByAssetIdAndFieldDefinitionId(assetId, fieldDefinitionId);
    }
}
