package com.assetflow.audit.application;

import com.assetflow.audit.domain.AuditDiscrepancyResolution;
import com.assetflow.audit.domain.AuditDiscrepancyResolutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditDiscrepancyResolutionUseCase {

    private final AuditDiscrepancyResolutionRepository repository;

    public AuditDiscrepancyResolutionUseCase(AuditDiscrepancyResolutionRepository repository) {
        this.repository = repository;
    }

    public AuditDiscrepancyResolution resolveDiscrepancy(
            UUID organizationId,
            UUID auditCycleId,
            UUID assetId,
            String discrepancyTypeStr,
            String resolutionActionStr,
            UUID resolvedBy,
            String notes) {
        AuditDiscrepancyResolution.DiscrepancyType type = AuditDiscrepancyResolution.DiscrepancyType.valueOf(discrepancyTypeStr.toUpperCase());
        AuditDiscrepancyResolution.ResolutionAction action = AuditDiscrepancyResolution.ResolutionAction.valueOf(resolutionActionStr.toUpperCase());

        AuditDiscrepancyResolution resolution = AuditDiscrepancyResolution.resolve(
                organizationId,
                auditCycleId,
                assetId,
                type,
                action,
                resolvedBy,
                notes
        );
        return repository.save(resolution);
    }

    public List<AuditDiscrepancyResolution> getResolutionsForAudit(UUID organizationId, UUID auditCycleId) {
        return repository.findByOrganizationIdAndAuditCycleId(organizationId, auditCycleId);
    }
}
