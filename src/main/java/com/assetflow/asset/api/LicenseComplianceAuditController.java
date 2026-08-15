package com.assetflow.asset.api;

import com.assetflow.asset.application.LicenseComplianceAuditUseCase;
import com.assetflow.asset.domain.LicenseComplianceAudit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/software-licenses/{licenseId}/compliance-audits")
public class LicenseComplianceAuditController {

    private final LicenseComplianceAuditUseCase useCase;

    public LicenseComplianceAuditController(LicenseComplianceAuditUseCase useCase) {
        this.useCase = useCase;
    }

    public record AuditRequest(
            int totalSeats,
            int allocatedSeats,
            String auditNotes
    ) {}

    public record AuditResponse(
            UUID id,
            UUID organizationId,
            UUID licenseId,
            int totalSeats,
            int allocatedSeats,
            String complianceStatus,
            String auditNotes,
            Instant auditedAt
    ) {}

    @PostMapping
    public ResponseEntity<AuditResponse> runAudit(
            @PathVariable UUID organizationId,
            @PathVariable UUID licenseId,
            @RequestBody AuditRequest request) {
        LicenseComplianceAudit audit = useCase.auditLicenseCompliance(
                organizationId,
                licenseId,
                request.totalSeats(),
                request.allocatedSeats(),
                request.auditNotes()
        );
        return ResponseEntity.ok(toResponse(audit));
    }

    @GetMapping
    public ResponseEntity<List<AuditResponse>> getAuditHistory(
            @PathVariable UUID organizationId,
            @PathVariable UUID licenseId) {
        List<AuditResponse> list = useCase.getAuditHistory(organizationId, licenseId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private AuditResponse toResponse(LicenseComplianceAudit a) {
        return new AuditResponse(
                a.getId(),
                a.getOrganizationId(),
                a.getLicenseId(),
                a.getTotalSeats(),
                a.getAllocatedSeats(),
                a.getComplianceStatus().name(),
                a.getAuditNotes(),
                a.getAuditedAt()
        );
    }
}
