package com.assetflow.financial.api;

import com.assetflow.financial.application.AssetTaxWriteOffUseCase;
import com.assetflow.financial.domain.AssetTaxWriteOff;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}")
public class AssetTaxWriteOffController {

    private final AssetTaxWriteOffUseCase useCase;

    public AssetTaxWriteOffController(AssetTaxWriteOffUseCase useCase) {
        this.useCase = useCase;
    }

    public record WriteOffRequest(
            int fiscalYear,
            BigDecimal currentTaxBasis,
            BigDecimal writeoffAmount,
            String writeoffReason
    ) {}

    public record WriteOffResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal writeoffAmount,
            BigDecimal remainingTaxBasis,
            String writeoffReason,
            String status,
            UUID approvedBy,
            Instant approvedAt,
            Instant createdAt
    ) {}

    @PostMapping("/assets/{assetId}/tax-writeoffs")
    public ResponseEntity<WriteOffResponse> executeWriteOff(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody WriteOffRequest request) {
        AssetTaxWriteOff writeOff = useCase.executeWriteOff(
                organizationId,
                assetId,
                request.fiscalYear(),
                request.currentTaxBasis(),
                request.writeoffAmount(),
                request.writeoffReason()
        );
        return ResponseEntity.ok(toResponse(writeOff));
    }

    @GetMapping("/assets/{assetId}/tax-writeoffs")
    public ResponseEntity<List<WriteOffResponse>> getWriteOffHistory(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<WriteOffResponse> list = useCase.getWriteOffHistory(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/tax-writeoffs/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_DIRECTOR')")
    public ResponseEntity<WriteOffResponse> approveWriteOff(
            @PathVariable UUID organizationId,
            @PathVariable UUID id,
            @RequestParam UUID approverUserId) {
        AssetTaxWriteOff approved = useCase.approveWriteOff(organizationId, id, approverUserId);
        return ResponseEntity.ok(toResponse(approved));
    }

    @PostMapping("/tax-writeoffs/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_DIRECTOR')")
    public ResponseEntity<WriteOffResponse> rejectWriteOff(
            @PathVariable UUID organizationId,
            @PathVariable UUID id,
            @RequestParam UUID approverUserId) {
        AssetTaxWriteOff rejected = useCase.rejectWriteOff(organizationId, id, approverUserId);
        return ResponseEntity.ok(toResponse(rejected));
    }

    private WriteOffResponse toResponse(AssetTaxWriteOff w) {
        return new WriteOffResponse(
                w.getId(),
                w.getOrganizationId(),
                w.getAssetId(),
                w.getFiscalYear(),
                w.getWriteoffAmount(),
                w.getRemainingTaxBasis(),
                w.getWriteoffReason(),
                w.getStatus().name(),
                w.getApprovedBy(),
                w.getApprovedAt(),
                w.getCreatedAt()
        );
    }
}
