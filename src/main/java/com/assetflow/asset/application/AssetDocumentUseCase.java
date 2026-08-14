package com.assetflow.asset.application;

import com.assetflow.asset.api.dto.RegisterAssetDocumentRequest;
import com.assetflow.asset.domain.AssetDocument;
import com.assetflow.asset.domain.AssetDocumentRepository;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssetDocumentUseCase {

    private final AssetDocumentRepository assetDocumentRepository;
    private final AssetRepository assetRepository;

    public AssetDocumentUseCase(AssetDocumentRepository assetDocumentRepository, AssetRepository assetRepository) {
        this.assetDocumentRepository = assetDocumentRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AssetDocument registerDocument(RegisterAssetDocumentRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        assetRepository.findById(request.getAssetId())
                .filter(asset -> asset.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        AssetDocument document = new AssetDocument(
                organizationId,
                request.getAssetId(),
                request.getFileName(),
                request.getFileType(),
                request.getFileSize(),
                request.getFileUrl(),
                request.getDocumentType(),
                request.getDescription()
        );

        return assetDocumentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> listDocuments(UUID assetId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return assetDocumentRepository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
