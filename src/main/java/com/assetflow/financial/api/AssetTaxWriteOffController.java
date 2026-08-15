package com.assetflow.financial.api;

import com.assetflow.financial.application.AssetTaxWriteOffUseCase;
import com.assetflow.financial.domain.AssetTaxWriteOff;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/assets/{assetId}/tax-writeoffs")
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
            Instant createdAt
    ) {}

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<WriteOffResponse>> getWriteOffHistory(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<WriteOffResponse> list = useCase.getWriteOffHistory(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
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
                w.getCreatedAt()
        );
    }
}
