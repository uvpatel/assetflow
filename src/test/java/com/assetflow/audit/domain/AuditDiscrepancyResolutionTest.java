package com.assetflow.audit.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditDiscrepancyResolutionTest {

    @Test
    void shouldCreateResolutionCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID auditCycleId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID resolvedBy = UUID.randomUUID();

        AuditDiscrepancyResolution resolution = AuditDiscrepancyResolution.resolve(
                orgId,
                auditCycleId,
                assetId,
                AuditDiscrepancyResolution.DiscrepancyType.MISSING_ASSET,
                AuditDiscrepancyResolution.ResolutionAction.MARK_AS_LOST,
                resolvedBy,
                "Verified missing during physical count"
        );

        assertThat(resolution.getId()).isNotNull();
        assertThat(resolution.getOrganizationId()).isEqualTo(orgId);
        assertThat(resolution.getAuditCycleId()).isEqualTo(auditCycleId);
        assertThat(resolution.getAssetId()).isEqualTo(assetId);
        assertThat(resolution.getDiscrepancyType()).isEqualTo(AuditDiscrepancyResolution.DiscrepancyType.MISSING_ASSET);
        assertThat(resolution.getResolutionAction()).isEqualTo(AuditDiscrepancyResolution.ResolutionAction.MARK_AS_LOST);
        assertThat(resolution.getResolvedBy()).isEqualTo(resolvedBy);
        assertThat(resolution.getNotes()).isEqualTo("Verified missing during physical count");
        assertThat(resolution.getResolvedAt()).isNotNull();
    }
}
