package com.assetflow.asset.domain;

import java.util.List;
import java.util.UUID;

public interface LicenseComplianceAuditRepository {
    LicenseComplianceAudit save(LicenseComplianceAudit audit);
    List<LicenseComplianceAudit> findByOrganizationIdAndLicenseId(UUID organizationId, UUID licenseId);
}
