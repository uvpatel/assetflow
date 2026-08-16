package com.assetflow.asset.application;

import com.assetflow.asset.domain.SoftwareLicense;
import com.assetflow.asset.domain.SoftwareLicenseAllocation;
import com.assetflow.asset.domain.SoftwareLicenseAllocationRepository;
import com.assetflow.asset.domain.SoftwareLicenseRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SoftwareLicenseUseCase {

    private static final Logger log = LoggerFactory.getLogger(SoftwareLicenseUseCase.class);

    private final SoftwareLicenseRepository repository;
    private final SoftwareLicenseAllocationRepository allocationRepository;

    public SoftwareLicenseUseCase(SoftwareLicenseRepository repository,
                                  SoftwareLicenseAllocationRepository allocationRepository) {
        this.repository = repository;
        this.allocationRepository = allocationRepository;
    }

    public record ComplianceAuditReport(
            UUID organizationId,
            int totalLicensesAudited,
            int compliantLicenses,
            int nonCompliantLicenses,
            int expiredLicenses,
            List<String> auditFindings
    ) {}

    @Transactional
    public SoftwareLicense registerLicense(
            UUID organizationId,
            String softwareName,
            String licenseKey,
            int totalSeats,
            LocalDate expirationDate) {
        SoftwareLicense license = SoftwareLicense.create(
                organizationId,
                softwareName,
                licenseKey,
                totalSeats,
                expirationDate
        );
        log.info("Registered software license {} for org {}", softwareName, organizationId);
        return repository.save(license);
    }

    @Transactional
    public SoftwareLicenseAllocation allocateSeatToEmployee(UUID organizationId, UUID licenseId, UUID employeeId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new DomainViolationException("License Not Found", "Target software license was not found."));

        if (!license.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot allocate seat for license belonging to another organization.");
        }

        if (allocationRepository.findBySoftwareLicenseIdAndEmployeeId(licenseId, employeeId).isPresent()) {
            throw new DomainViolationException("Seat Already Allocated", "Employee already holds a seat for this license.");
        }

        license.allocateSeat();
        repository.save(license);

        SoftwareLicenseAllocation allocation = new SoftwareLicenseAllocation(organizationId, licenseId, employeeId);
        log.info("Allocated seat for license {} to employee {}", licenseId, employeeId);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public void revokeSeatFromEmployee(UUID organizationId, UUID licenseId, UUID employeeId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new DomainViolationException("License Not Found", "Target software license was not found."));

        if (!license.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot revoke seat for license belonging to another organization.");
        }

        SoftwareLicenseAllocation allocation = allocationRepository.findBySoftwareLicenseIdAndEmployeeId(licenseId, employeeId)
                .orElseThrow(() -> new DomainViolationException("Allocation Not Found", "No active seat allocation found for employee."));

        license.revokeSeat();
        repository.save(license);

        allocationRepository.delete(allocation);
        log.info("Revoked seat for license {} from employee {}", licenseId, employeeId);
    }

    @Transactional
    public SoftwareLicense allocateSeat(UUID licenseId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new DomainViolationException("License Not Found", "Software license not found: " + licenseId));
        license.allocateSeat();
        return repository.save(license);
    }

    @Transactional
    public SoftwareLicense revokeSeat(UUID licenseId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new DomainViolationException("License Not Found", "Software license not found: " + licenseId));
        license.revokeSeat();
        return repository.save(license);
    }

    @Transactional(readOnly = true)
    public ComplianceAuditReport performComplianceAudit(UUID organizationId) {
        List<SoftwareLicense> licenses = repository.findByOrganizationId(organizationId);
        LocalDate today = LocalDate.now();

        int compliant = 0;
        int nonCompliant = 0;
        int expired = 0;
        List<String> findings = new java.util.ArrayList<>();

        for (SoftwareLicense lic : licenses) {
            boolean isExpired = lic.getExpirationDate() != null && lic.getExpirationDate().isBefore(today);
            boolean isOverAllocated = lic.getAllocatedSeats() > lic.getTotalSeats();

            if (isExpired) {
                expired++;
                findings.add("License '" + lic.getSoftwareName() + "' (ID: " + lic.getId() + ") expired on " + lic.getExpirationDate());
            }

            if (isOverAllocated) {
                nonCompliant++;
                findings.add("License '" + lic.getSoftwareName() + "' (ID: " + lic.getId() + ") is over-allocated: " + lic.getAllocatedSeats() + "/" + lic.getTotalSeats() + " seats");
            } else if (!isExpired) {
                compliant++;
            }
        }

        log.info("Completed license compliance audit for org {}: {} compliant, {} non-compliant, {} expired",
                organizationId, compliant, nonCompliant, expired);

        return new ComplianceAuditReport(organizationId, licenses.size(), compliant, nonCompliant, expired, findings);
    }

    @Transactional(readOnly = true)
    public List<SoftwareLicense> getLicenses(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
