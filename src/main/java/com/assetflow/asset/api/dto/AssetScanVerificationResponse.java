package com.assetflow.asset.api.dto;

import com.assetflow.asset.domain.Asset;

import java.time.Instant;
import java.util.UUID;

public record AssetScanVerificationResponse(
        UUID assetId,
        UUID organizationId,
        String name,
        String assetTag,
        String serialNumber,
        String status,
        UUID categoryId,
        String location,
        Instant verifiedAt
) {
    public static AssetScanVerificationResponse fromDomain(Asset asset) {
        return new AssetScanVerificationResponse(
                asset.getId(),
                asset.getOrganizationId(),
                asset.getName(),
                asset.getAssetTag(),
                asset.getSerialNumber(),
                asset.getStatus().name(),
                asset.getCategoryId(),
                asset.getLocation(),
                Instant.now()
        );
    }
}
