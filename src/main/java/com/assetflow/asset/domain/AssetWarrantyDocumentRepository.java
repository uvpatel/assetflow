package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetWarrantyDocumentRepository {
    AssetWarrantyDocument save(AssetWarrantyDocument document);
    Optional<AssetWarrantyDocument> findById(UUID id);
    List<AssetWarrantyDocument> findByWarrantyId(UUID warrantyId);
}
