package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SoftwareLicenseAllocationTest {

    @Test
    @DisplayName("Should create software license allocation record for employee")
    void shouldCreateSoftwareLicenseAllocation() {
        UUID orgId = UUID.randomUUID();
        UUID licenseId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        SoftwareLicenseAllocation allocation = new SoftwareLicenseAllocation(orgId, licenseId, employeeId);

        assertThat(allocation.getOrganizationId()).isEqualTo(orgId);
        assertThat(allocation.getSoftwareLicenseId()).isEqualTo(licenseId);
        assertThat(allocation.getEmployeeId()).isEqualTo(employeeId);
        assertThat(allocation.getStatus()).isEqualTo("ACTIVE");

        allocation.revoke();
        assertThat(allocation.getStatus()).isEqualTo("REVOKED");
    }
}
