package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetWarrantyUseCase;
import com.assetflow.asset.domain.AssetWarranty;
import com.assetflow.asset.domain.AssetWarrantyDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}")
public class AssetWarrantyController {

    private final AssetWarrantyUseCase useCase;

    public AssetWarrantyController(AssetWarrantyUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateWarrantyRequest(
            UUID assetId,
            String providerName,
            String policyNumber,
            Instant startDate,
            Instant endDate,
            String coverageDetails
    ) {}

    public record AttachWarrantyDocumentRequest(
            String documentName,
            String fileType,
            long fileSizeBytes,
            String storageUrl
    ) {}

    public record ExpiryAlertDispatchResponse(
            UUID organizationId,
            int daysAhead,
            int alertsDispatched
    ) {}

    public record WarrantyResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String providerName,
            String policyNumber,
            Instant startDate,
            Instant endDate,
            String coverageDetails,
            boolean isExpired,
            boolean isExpiringSoon
    ) {}

    @PostMapping("/assets/{assetId}/warranties")
    public ResponseEntity<WarrantyResponse> createWarranty(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody CreateWarrantyRequest request) {
        AssetWarranty warranty = useCase.createWarranty(
                organizationId,
                assetId,
                request.providerName(),
                request.policyNumber(),
                request.startDate(),
                request.endDate(),
                request.coverageDetails()
        );
        return ResponseEntity.ok(toResponse(warranty));
    }

    @PostMapping("/warranties/{warrantyId}/documents")
    public ResponseEntity<AssetWarrantyDocument> attachWarrantyDocument(
            @PathVariable UUID organizationId,
            @PathVariable UUID warrantyId,
            @RequestBody AttachWarrantyDocumentRequest request) {
        AssetWarrantyDocument doc = useCase.attachWarrantyDocument(
                organizationId,
                warrantyId,
                request.documentName(),
                request.fileType(),
                request.fileSizeBytes(),
                request.storageUrl()
        );
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/warranties/{warrantyId}/documents")
    public ResponseEntity<List<AssetWarrantyDocument>> getWarrantyDocuments(
            @PathVariable UUID organizationId,
            @PathVariable UUID warrantyId) {
        List<AssetWarrantyDocument> list = useCase.getWarrantyDocuments(organizationId, warrantyId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/warranties/dispatch-expiry-alerts")
    public ResponseEntity<ExpiryAlertDispatchResponse> dispatchExpiryAlerts(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "30") int daysAhead) {
        int count = useCase.dispatchWarrantyExpiryAlerts(organizationId, daysAhead);
        return ResponseEntity.ok(new ExpiryAlertDispatchResponse(organizationId, daysAhead, count));
    }

    @GetMapping("/assets/{assetId}/warranties")
    public ResponseEntity<List<WarrantyResponse>> getAssetWarranties(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<WarrantyResponse> list = useCase.getWarrantiesForAsset(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/warranties/expiring")
    public ResponseEntity<List<WarrantyResponse>> getExpiringWarranties(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "30") int daysAhead) {
        List<WarrantyResponse> list = useCase.getExpiringWarranties(organizationId, daysAhead)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private WarrantyResponse toResponse(AssetWarranty w) {
        Instant now = Instant.now();
        return new WarrantyResponse(
                w.getId(),
                w.getOrganizationId(),
                w.getAssetId(),
                w.getProviderName(),
                w.getPolicyNumber(),
                w.getStartDate(),
                w.getEndDate(),
                w.getCoverageDetails(),
                w.isExpired(now),
                w.isExpiringWithin(now, 30)
        );
    }
}
