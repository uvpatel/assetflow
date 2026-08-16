package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetDisposalUseCase;
import com.assetflow.asset.domain.AssetDisposalRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}")
public class AssetDisposalController {

    private final AssetDisposalUseCase useCase;

    public AssetDisposalController(AssetDisposalUseCase useCase) {
        this.useCase = useCase;
    }

    public record InitiateDisposalRequest(
            String disposalType,
            String disposalReason,
            BigDecimal proceedsAmount,
            String currency,
            UUID authorizedByUserId,
            String certificateReference
    ) {}

    public record ApproveDisposalRequest(
            UUID authorizedByUserId
    ) {}

    public record DisposalResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String disposalType,
            Instant disposalDate,
            BigDecimal proceedsAmount,
            String currency,
            String disposalReason,
            UUID authorizedByUserId,
            String certificateReference,
            String status
    ) {}

    @PostMapping("/assets/{assetId}/disposals")
    public ResponseEntity<DisposalResponse> initiateDisposal(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody InitiateDisposalRequest request) {
        AssetDisposalRecord.DisposalType type = request.disposalType() != null
                ? AssetDisposalRecord.DisposalType.valueOf(request.disposalType().toUpperCase())
                : AssetDisposalRecord.DisposalType.SCRAP;

        AssetDisposalRecord record = useCase.initiateDisposal(
                organizationId,
                assetId,
                type,
                request.disposalReason(),
                request.proceedsAmount(),
                request.currency(),
                request.authorizedByUserId(),
                request.certificateReference()
        );
        return ResponseEntity.ok(toResponse(record));
    }

    @PostMapping("/asset-disposals/{disposalId}/approve")
    public ResponseEntity<DisposalResponse> approveDisposal(
            @PathVariable UUID organizationId,
            @PathVariable UUID disposalId,
            @RequestBody(required = false) ApproveDisposalRequest request) {
        UUID authorizedBy = request != null ? request.authorizedByUserId() : null;
        AssetDisposalRecord record = useCase.approveDisposal(organizationId, disposalId, authorizedBy);
        return ResponseEntity.ok(toResponse(record));
    }

    @PostMapping("/asset-disposals/{disposalId}/execute")
    public ResponseEntity<DisposalResponse> executeDisposal(
            @PathVariable UUID organizationId,
            @PathVariable UUID disposalId) {
        AssetDisposalRecord record = useCase.executeDisposal(organizationId, disposalId);
        return ResponseEntity.ok(toResponse(record));
    }

    @PostMapping("/asset-disposals/{disposalId}/cancel")
    public ResponseEntity<DisposalResponse> cancelDisposal(
            @PathVariable UUID organizationId,
            @PathVariable UUID disposalId) {
        AssetDisposalRecord record = useCase.cancelDisposal(organizationId, disposalId);
        return ResponseEntity.ok(toResponse(record));
    }

    @GetMapping("/assets/{assetId}/disposals")
    public ResponseEntity<List<DisposalResponse>> getDisposalsForAsset(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<DisposalResponse> list = useCase.getDisposalsForAsset(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/asset-disposals")
    public ResponseEntity<List<DisposalResponse>> getDisposalsForOrganization(
            @PathVariable UUID organizationId) {
        List<DisposalResponse> list = useCase.getDisposalsForOrganization(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/asset-disposals/{disposalId}")
    public ResponseEntity<DisposalResponse> getDisposalById(
            @PathVariable UUID organizationId,
            @PathVariable UUID disposalId) {
        return useCase.getDisposalById(organizationId, disposalId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DisposalResponse toResponse(AssetDisposalRecord r) {
        return new DisposalResponse(
                r.getId(),
                r.getOrganizationId(),
                r.getAssetId(),
                r.getDisposalType().name(),
                r.getDisposalDate(),
                r.getProceedsAmount(),
                r.getCurrency(),
                r.getDisposalReason(),
                r.getAuthorizedByUserId(),
                r.getCertificateReference(),
                r.getStatus().name()
        );
    }
}
