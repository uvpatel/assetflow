package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetWarrantyDocument;
import com.assetflow.asset.domain.AssetWarrantyDocumentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetWarrantyDocumentRepositoryAdapter implements AssetWarrantyDocumentRepository {

    private final AssetWarrantyDocumentJpaRepository jpaRepository;

    public JpaAssetWarrantyDocumentRepositoryAdapter(AssetWarrantyDocumentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AssetWarrantyDocument save(AssetWarrantyDocument document) {
        return jpaRepository.save(document);
    }

    @Override
    public Optional<AssetWarrantyDocument> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AssetWarrantyDocument> findByWarrantyId(UUID warrantyId) {
        return jpaRepository.findByWarrantyId(warrantyId);
    }
}
