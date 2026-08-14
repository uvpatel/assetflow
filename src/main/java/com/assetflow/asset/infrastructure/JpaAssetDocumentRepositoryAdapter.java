package com.assetflow.asset.infrastructure;

import com.assetflow.asset.domain.AssetDocument;
import com.assetflow.asset.domain.AssetDocumentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAssetDocumentRepository extends JpaRepository<AssetDocument, UUID> {
    List<AssetDocument> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId);
}

@Repository
public class JpaAssetDocumentRepositoryAdapter implements AssetDocumentRepository {

    private final SpringDataAssetDocumentRepository repository;

    public JpaAssetDocumentRepositoryAdapter(SpringDataAssetDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetDocument save(AssetDocument assetDocument) {
        return repository.save(assetDocument);
    }

    @Override
    public Optional<AssetDocument> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AssetDocument> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
