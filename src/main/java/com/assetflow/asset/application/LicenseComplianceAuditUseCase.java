package com.assetflow.asset.application;

import com.assetflow.asset.domain.LicenseComplianceAudit;
import com.assetflow.asset.domain.LicenseComplianceAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseComplianceAuditUseCase {

    private final LicenseComplianceAuditRepository repository;

    public LicenseComplianceAuditUseCase(LicenseComplianceAuditRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LicenseComplianceAudit auditLicenseCompliance(
            UUID organizationId,
            UUID licenseId,
            int totalSeats,
            int allocatedSeats,
            String auditNotes) {
        LicenseComplianceAudit audit = LicenseComplianceAudit.performAudit(
                organizationId,
                licenseId,
                totalSeats,
                allocatedSeats,
                auditNotes
        );
        return repository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<LicenseComplianceAudit> getAuditHistory(UUID organizationId, UUID licenseId) {
        return repository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
    }
}
