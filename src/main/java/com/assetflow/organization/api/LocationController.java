package com.assetflow.organization.api;

import com.assetflow.organization.api.dto.CreateLocationRequest;
import com.assetflow.organization.api.dto.LocationResponse;
import com.assetflow.organization.api.dto.LocationTreeNodeDto;
import com.assetflow.organization.api.dto.UpdateLocationRequest;
import com.assetflow.organization.application.LocationUseCase;
import com.assetflow.organization.domain.LocationType;
import com.assetflow.shared.security.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Location Hierarchy", description = "Endpoints for managing physical organizational locations (Campus, Building, Floor, Room, Storage Area)")
public class LocationController {

    private final LocationUseCase locationUseCase;

    public LocationController(LocationUseCase locationUseCase) {
        this.locationUseCase = locationUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION_OWNER', 'ASSET_MANAGER')")
    @Operation(summary = "Create a physical location node")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        UUID orgId = TenantContext.getOrganizationId();
        LocationResponse response = locationUseCase.createLocation(orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all organization locations with optional type filter")
    public ResponseEntity<List<LocationResponse>> listLocations(@RequestParam(required = false) LocationType type) {
        UUID orgId = TenantContext.getOrganizationId();
        List<LocationResponse> response = locationUseCase.listLocations(orgId, Optional.ofNullable(type));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tree")
    @Operation(summary = "Get full hierarchical location tree")
    public ResponseEntity<List<LocationTreeNodeDto>> getLocationTree() {
        UUID orgId = TenantContext.getOrganizationId();
        List<LocationTreeNodeDto> response = locationUseCase.buildLocationTree(orgId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location details by ID")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable UUID id) {
        UUID orgId = TenantContext.getOrganizationId();
        LocationResponse response = locationUseCase.getLocationById(orgId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/sub-locations")
    @Operation(summary = "Get immediate child locations")
    public ResponseEntity<List<LocationResponse>> getSubLocations(@PathVariable UUID id) {
        UUID orgId = TenantContext.getOrganizationId();
        List<LocationResponse> response = locationUseCase.listSubLocations(orgId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION_OWNER', 'ASSET_MANAGER')")
    @Operation(summary = "Update location metadata")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable UUID id,
                                                          @Valid @RequestBody UpdateLocationRequest request) {
        UUID orgId = TenantContext.getOrganizationId();
        LocationResponse response = locationUseCase.updateLocation(orgId, id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION_OWNER', 'ASSET_MANAGER')")
    @Operation(summary = "Deactivate a location")
    public ResponseEntity<LocationResponse> deactivateLocation(@PathVariable UUID id) {
        UUID orgId = TenantContext.getOrganizationId();
        LocationResponse response = locationUseCase.deactivateLocation(orgId, id);
        return ResponseEntity.ok(response);
    }
}
