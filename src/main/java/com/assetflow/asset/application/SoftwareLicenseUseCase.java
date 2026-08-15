package com.assetflow.asset.application;

import com.assetflow.asset.domain.SoftwareLicense;
import com.assetflow.asset.domain.SoftwareLicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SoftwareLicenseUseCase {

    private final SoftwareLicenseRepository repository;

    public SoftwareLicenseUseCase(SoftwareLicenseRepository repository) {
        this.repository = repository;
    }

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
        return repository.save(license);
    }

    @Transactional
    public SoftwareLicense allocateSeat(UUID licenseId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found: " + licenseId));
        license.allocateSeat();
        return repository.save(license);
    }

    @Transactional
    public SoftwareLicense revokeSeat(UUID licenseId) {
        SoftwareLicense license = repository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found: " + licenseId));
        license.revokeSeat();
        return repository.save(license);
    }

    @Transactional(readOnly = true)
    public List<SoftwareLicense> getLicenses(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
