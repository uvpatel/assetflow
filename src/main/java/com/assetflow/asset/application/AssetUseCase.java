package com.assetflow.asset.application;

import com.assetflow.asset.api.dto.RegisterAssetRequest;
import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssetUseCase {

    private final AssetRepository assetRepository;

    public AssetUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional
    public Asset registerAsset(RegisterAssetRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        if (assetRepository.existsByOrganizationIdAndAssetTag(organizationId, request.getAssetTag())) {
            throw new DomainViolationException("Asset with tag " + request.getAssetTag() + " already exists");
        }

        Asset asset = new Asset(
                organizationId,
                request.getCategoryId(),
                request.getAssetTag(),
                request.getName(),
                request.getDescription(),
                request.getSerialNumber(),
                request.getLocation()
        );

        return assetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public List<Asset> listAssets() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return assetRepository.findByOrganizationId(organizationId);
    }
}
