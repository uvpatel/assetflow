package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetWarrantyUseCase;
import com.assetflow.asset.domain.AssetWarranty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
