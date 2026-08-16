package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetWarranty;
import com.assetflow.asset.domain.AssetWarrantyDocument;
import com.assetflow.asset.domain.AssetWarrantyDocumentRepository;
import com.assetflow.asset.domain.AssetWarrantyRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AssetWarrantyUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetWarrantyUseCase.class);

    private final AssetWarrantyRepository repository;
    private final AssetWarrantyDocumentRepository documentRepository;

    public AssetWarrantyUseCase(AssetWarrantyRepository repository,
                                AssetWarrantyDocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
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
        log.info("Created warranty policy {} for asset {} in org {}", policyNumber, assetId, organizationId);
        return repository.save(warranty);
    }

    @Transactional
    public AssetWarrantyDocument attachWarrantyDocument(
            UUID organizationId,
            UUID warrantyId,
            String documentName,
            String fileType,
            long fileSizeBytes,
            String storageUrl) {
        AssetWarranty warranty = repository.findById(warrantyId)
                .orElseThrow(() -> new DomainViolationException("Warranty Not Found", "Target warranty policy was not found."));

        if (!warranty.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot attach document to warranty belonging to another organization.");
        }

        AssetWarrantyDocument document = new AssetWarrantyDocument(
                organizationId,
                warrantyId,
                documentName,
                fileType,
                fileSizeBytes,
                storageUrl
        );

        log.info("Attached warranty document {} (Size: {} bytes) to warranty {}", documentName, fileSizeBytes, warrantyId);
        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<AssetWarrantyDocument> getWarrantyDocuments(UUID organizationId, UUID warrantyId) {
        return documentRepository.findByWarrantyId(warrantyId);
    }

    @Transactional
    public int dispatchWarrantyExpiryAlerts(UUID organizationId, int daysAhead) {
        List<AssetWarranty> expiringWarranties = getExpiringWarranties(organizationId, daysAhead);
        int alertsDispatched = 0;

        for (AssetWarranty w : expiringWarranties) {
            log.info("Dispatched warranty expiration alert for asset {} (Policy: {}, Expiring: {})",
                    w.getAssetId(), w.getPolicyNumber(), w.getEndDate());
            alertsDispatched++;
        }

        return alertsDispatched;
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
