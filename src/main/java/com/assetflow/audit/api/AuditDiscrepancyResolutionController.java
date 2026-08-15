package com.assetflow.audit.api;

import com.assetflow.audit.application.AuditDiscrepancyResolutionUseCase;
import com.assetflow.audit.domain.AuditDiscrepancyResolution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/audits/{auditCycleId}/resolutions")
public class AuditDiscrepancyResolutionController {

    private final AuditDiscrepancyResolutionUseCase useCase;

    public AuditDiscrepancyResolutionController(AuditDiscrepancyResolutionUseCase useCase) {
        this.useCase = useCase;
    }

    public record ResolveDiscrepancyRequest(
            UUID assetId,
            String discrepancyType,
            String resolutionAction,
            UUID resolvedBy,
            String notes
    ) {}

    public record DiscrepancyResolutionResponse(
            UUID id,
            UUID organizationId,
            UUID auditCycleId,
            UUID assetId,
            String discrepancyType,
            String resolutionAction,
            UUID resolvedBy,
            String notes,
            Instant resolvedAt
    ) {}

    @PostMapping
    public ResponseEntity<DiscrepancyResolutionResponse> resolveDiscrepancy(
            @PathVariable UUID organizationId,
            @PathVariable UUID auditCycleId,
            @RequestBody ResolveDiscrepancyRequest request) {
        AuditDiscrepancyResolution resolution = useCase.resolveDiscrepancy(
                organizationId,
                auditCycleId,
                request.assetId(),
                request.discrepancyType(),
                request.resolutionAction(),
                request.resolvedBy(),
                request.notes()
        );
        return ResponseEntity.ok(toResponse(resolution));
    }

    @GetMapping
    public ResponseEntity<List<DiscrepancyResolutionResponse>> getResolutions(
            @PathVariable UUID organizationId,
            @PathVariable UUID auditCycleId) {
        List<DiscrepancyResolutionResponse> list = useCase.getResolutionsForAudit(organizationId, auditCycleId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private DiscrepancyResolutionResponse toResponse(AuditDiscrepancyResolution r) {
        return new DiscrepancyResolutionResponse(
                r.getId(),
                r.getOrganizationId(),
                r.getAuditCycleId(),
                r.getAssetId(),
                r.getDiscrepancyType().name(),
                r.getResolutionAction().name(),
                r.getResolvedBy(),
                r.getNotes(),
                r.getResolvedAt()
        );
    }
}
