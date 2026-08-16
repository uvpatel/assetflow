package com.assetflow.asset.application;

import com.assetflow.asset.domain.SoftwareLicense;
import com.assetflow.asset.domain.SoftwareLicenseAllocation;
import com.assetflow.asset.domain.SoftwareLicenseAllocationRepository;
import com.assetflow.asset.domain.SoftwareLicenseRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoftwareLicenseUseCaseTest {

    @Mock
    private SoftwareLicenseRepository repository;

    @Mock
    private SoftwareLicenseAllocationRepository allocationRepository;

    private SoftwareLicenseUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new SoftwareLicenseUseCase(repository, allocationRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should allocate software license seat to employee and increment allocated seats")
    void shouldAllocateSeatToEmployee() {
        UUID licenseId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        SoftwareLicense license = SoftwareLicense.create(orgId, "IntelliJ IDEA", "KEY-123", 10, LocalDate.now().plusYears(1));

        when(repository.findById(licenseId)).thenReturn(Optional.of(license));
        when(allocationRepository.findBySoftwareLicenseIdAndEmployeeId(licenseId, employeeId)).thenReturn(Optional.empty());
        when(repository.save(any(SoftwareLicense.class))).thenAnswer(inv -> inv.getArgument(0));
        when(allocationRepository.save(any(SoftwareLicenseAllocation.class))).thenAnswer(inv -> inv.getArgument(0));

        SoftwareLicenseAllocation allocation = useCase.allocateSeatToEmployee(orgId, licenseId, employeeId);

        assertThat(allocation.getEmployeeId()).isEqualTo(employeeId);
        assertThat(license.getAllocatedSeats()).isEqualTo(1);
        verify(repository).save(license);
        verify(allocationRepository).save(any(SoftwareLicenseAllocation.class));
    }

    @Test
    @DisplayName("Should perform compliance audit and detect expired licenses")
    void shouldPerformComplianceAudit() {
        SoftwareLicense validLicense = SoftwareLicense.create(orgId, "Jira", "JIRA-1", 5, LocalDate.now().plusMonths(6));
        SoftwareLicense expiredLicense = SoftwareLicense.create(orgId, "Confluence", "CONF-1", 5, LocalDate.now().minusDays(10));

        when(repository.findByOrganizationId(orgId)).thenReturn(List.of(validLicense, expiredLicense));

        SoftwareLicenseUseCase.ComplianceAuditReport report = useCase.performComplianceAudit(orgId);

        assertThat(report.totalLicensesAudited()).isEqualTo(2);
        assertThat(report.compliantLicenses()).isEqualTo(1);
        assertThat(report.expiredLicenses()).isEqualTo(1);
        assertThat(report.auditFindings()).hasSize(1);
    }
}
