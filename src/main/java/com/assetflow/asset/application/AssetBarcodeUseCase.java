package com.assetflow.asset.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetBarcode;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AssetBarcodeUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetBarcodeUseCase.class);
    private final AssetRepository assetRepository;

    public AssetBarcodeUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public AssetBarcode generateBarcode(UUID organizationId, UUID assetId, String assetTag) {
        return AssetBarcode.generateForAsset(organizationId, assetId, assetTag);
    }

    @Transactional(readOnly = true)
    public Asset resolveScannedAsset(String rawCode) {
        UUID organizationId = TenantContext.requireOrganizationId();
        String cleanedCode = rawCode != null ? rawCode.trim() : "";

        if (cleanedCode.startsWith("AST-")) {
            cleanedCode = cleanedCode.substring(4);
        }

        final String searchTag = cleanedCode;

        // Try direct tag lookup first
        Optional<Asset> assetOpt = assetRepository.findByOrganizationIdAndAssetTag(organizationId, searchTag);

        // Try UUID lookup if tag lookup failed
        if (assetOpt.isEmpty()) {
            try {
                UUID candidateId = UUID.fromString(searchTag);
                assetOpt = assetRepository.findById(candidateId);
            } catch (IllegalArgumentException ignored) {
                // Not a valid UUID format
            }
        }

        Asset asset = assetOpt.orElseThrow(() -> new ResourceNotFoundException("Asset", searchTag));

        if (!asset.getOrganizationId().equals(organizationId)) {
            throw new ResourceNotFoundException("Asset", searchTag);
        }

        log.info("Barcode scan successfully resolved asset {} (Tag: {}) for organization {}", asset.getId(), asset.getAssetTag(), organizationId);
        return asset;
    }
}
