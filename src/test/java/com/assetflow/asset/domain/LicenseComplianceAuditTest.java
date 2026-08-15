package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseComplianceAuditTest {

    @Test
    void shouldEvaluateOverAllocatedStatusCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID licenseId = UUID.randomUUID();

        LicenseComplianceAudit audit = LicenseComplianceAudit.performAudit(
                orgId,
                licenseId,
                10,
                12,
                "Exceeded total seats by 2"
        );

        assertThat(audit.getId()).isNotNull();
        assertThat(audit.getComplianceStatus()).isEqualTo(LicenseComplianceAudit.ComplianceStatus.OVER_ALLOCATED);
    }

    @Test
    void shouldEvaluateAtCapacityStatusCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID licenseId = UUID.randomUUID();

        LicenseComplianceAudit audit = LicenseComplianceAudit.performAudit(
                orgId,
                licenseId,
                10,
                10,
                "Fully allocated"
        );

        assertThat(audit.getComplianceStatus()).isEqualTo(LicenseComplianceAudit.ComplianceStatus.AT_CAPACITY);
    }

    @Test
    void shouldEvaluateUnderUtilizedStatusCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID licenseId = UUID.randomUUID();

        LicenseComplianceAudit audit = LicenseComplianceAudit.performAudit(
                orgId,
                licenseId,
                10,
                4,
                "Low seat usage"
        );

        assertThat(audit.getComplianceStatus()).isEqualTo(LicenseComplianceAudit.ComplianceStatus.UNDER_UTILIZED);
    }

    @Test
    void shouldEvaluateCompliantStatusCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID licenseId = UUID.randomUUID();

        LicenseComplianceAudit audit = LicenseComplianceAudit.performAudit(
                orgId,
                licenseId,
                10,
                8,
                "Healthy seat allocation ratio"
        );

        assertThat(audit.getComplianceStatus()).isEqualTo(LicenseComplianceAudit.ComplianceStatus.COMPLIANT);
    }
}
