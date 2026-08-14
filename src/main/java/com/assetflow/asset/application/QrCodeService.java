package com.assetflow.asset.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QrCodeService {

    private final AssetRepository assetRepository;

    public QrCodeService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public String generateQrCodePayload(UUID assetId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        if (!asset.getOrganizationId().equals(organizationId)) {
            throw new ResourceNotFoundException("Asset", assetId);
        }

        return String.format("{\"orgId\":\"%s\",\"assetId\":\"%s\",\"tag\":\"%s\"}",
                organizationId, asset.getId(), asset.getAssetTag());
    }
}
