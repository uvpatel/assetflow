package com.assetflow.asset.api;

import com.assetflow.asset.application.SoftwareLicenseUseCase;
import com.assetflow.asset.domain.SoftwareLicense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/software-licenses")
public class SoftwareLicenseController {

    private final SoftwareLicenseUseCase useCase;

    public SoftwareLicenseController(SoftwareLicenseUseCase useCase) {
        this.useCase = useCase;
    }

    public record RegisterLicenseRequest(
            String softwareName,
            String licenseKey,
            int totalSeats,
            LocalDate expirationDate
    ) {}

    public record LicenseResponse(
            UUID id,
            UUID organizationId,
            String softwareName,
            String licenseKey,
            int totalSeats,
            int allocatedSeats,
            int availableSeats,
            LocalDate expirationDate
    ) {}

    @PostMapping
    public ResponseEntity<LicenseResponse> registerLicense(
            @PathVariable UUID organizationId,
            @RequestBody RegisterLicenseRequest request) {
        SoftwareLicense license = useCase.registerLicense(
                organizationId,
                request.softwareName(),
                request.licenseKey(),
                request.totalSeats(),
                request.expirationDate()
        );
        return ResponseEntity.ok(toResponse(license));
    }

    @PostMapping("/{licenseId}/allocate-seat")
    public ResponseEntity<LicenseResponse> allocateSeat(@PathVariable UUID licenseId) {
        SoftwareLicense license = useCase.allocateSeat(licenseId);
        return ResponseEntity.ok(toResponse(license));
    }

    @PostMapping("/{licenseId}/revoke-seat")
    public ResponseEntity<LicenseResponse> revokeSeat(@PathVariable UUID licenseId) {
        SoftwareLicense license = useCase.revokeSeat(licenseId);
        return ResponseEntity.ok(toResponse(license));
    }

    @GetMapping
    public ResponseEntity<List<LicenseResponse>> getLicenses(@PathVariable UUID organizationId) {
        List<LicenseResponse> list = useCase.getLicenses(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private LicenseResponse toResponse(SoftwareLicense l) {
        return new LicenseResponse(
                l.getId(),
                l.getOrganizationId(),
                l.getSoftwareName(),
                l.getLicenseKey(),
                l.getTotalSeats(),
                l.getAllocatedSeats(),
                l.getAvailableSeats(),
                l.getExpirationDate()
        );
    }
}
