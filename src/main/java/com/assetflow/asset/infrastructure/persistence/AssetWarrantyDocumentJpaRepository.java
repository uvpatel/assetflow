package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetWarrantyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetWarrantyDocumentJpaRepository extends JpaRepository<AssetWarrantyDocument, UUID> {
    List<AssetWarrantyDocument> findByWarrantyId(UUID warrantyId);
}
