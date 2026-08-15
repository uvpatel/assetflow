package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetWarranty;
import com.assetflow.asset.domain.AssetWarrantyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AssetWarrantyUseCase {

    private final AssetWarrantyRepository repository;

    public AssetWarrantyUseCase(AssetWarrantyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AssetWarranty createWarranty(
            UUID organizationId,
            UUID assetId,
            String providerName,
            String policyNumber,
            Instant startDate,
            Instant endDate,
            String coverageDetails) {
        AssetWarranty warranty = AssetWarranty.create(
                organizationId,
                assetId,
                providerName,
                policyNumber,
                startDate,
                endDate,
                coverageDetails
        );
        return repository.save(warranty);
    }

    @Transactional(readOnly = true)
    public List<AssetWarranty> getWarrantiesForAsset(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }

    @Transactional(readOnly = true)
    public List<AssetWarranty> getExpiringWarranties(UUID organizationId, int daysAhead) {
        Instant threshold = Instant.now().plus(daysAhead, ChronoUnit.DAYS);
        return repository.findExpiringWarranties(organizationId, threshold);
    }
}
