package com.assetflow.organization.application;

import com.assetflow.organization.api.dto.CreateLocationRequest;
import com.assetflow.organization.api.dto.LocationResponse;
import com.assetflow.organization.api.dto.LocationTreeNodeDto;
import com.assetflow.organization.api.dto.UpdateLocationRequest;
import com.assetflow.organization.domain.Location;
import com.assetflow.organization.domain.LocationRepository;
import com.assetflow.organization.domain.LocationType;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationUseCase {

    private final LocationRepository locationRepository;

    public LocationUseCase(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public LocationResponse createLocation(UUID organizationId, CreateLocationRequest request) {
        String normalizedCode = request.code().trim().toUpperCase();

        if (locationRepository.existsByOrganizationIdAndCode(organizationId, normalizedCode)) {
            throw new DomainViolationException("Duplicate Location Code", "A location with code '" + normalizedCode + "' already exists in this organization.");
        }

        if (request.parentLocationId() != null) {
            Location parent = locationRepository.findById(request.parentLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Location", request.parentLocationId().toString()));

            if (!parent.getOrganizationId().equals(organizationId)) {
                throw new DomainViolationException("Invalid Parent Location", "Parent location does not belong to the same organization.");
            }
        }

        Location location = Location.create(
                organizationId,
                request.name(),
                normalizedCode,
                request.type(),
                request.parentLocationId(),
                request.address(),
                request.description()
        );

        Location saved = locationRepository.save(location);
        return LocationResponse.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocationById(UUID organizationId, UUID locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));

        if (!location.getOrganizationId().equals(organizationId)) {
            throw new ResourceNotFoundException("Location", locationId.toString());
        }

        return LocationResponse.fromDomain(location);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> listLocations(UUID organizationId, Optional<LocationType> typeFilter) {
        List<Location> locations = typeFilter.isPresent()
                ? locationRepository.findByOrganizationIdAndType(organizationId, typeFilter.get())
                : locationRepository.findByOrganizationId(organizationId);

        return locations.stream()
                .map(LocationResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> listSubLocations(UUID organizationId, UUID parentLocationId) {
        return locationRepository.findByOrganizationIdAndParentLocationId(organizationId, parentLocationId).stream()
                .map(LocationResponse::fromDomain)
                .toList();
    }

    @Transactional
    public LocationResponse updateLocation(UUID organizationId, UUID locationId, UpdateLocationRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));

        if (!location.getOrganizationId().equals(organizationId)) {
            throw new ResourceNotFoundException("Location", locationId.toString());
        }

        if (request.parentLocationId() != null) {
            if (request.parentLocationId().equals(locationId)) {
                throw new DomainViolationException("Circular Hierarchy", "A location cannot be its own parent.");
            }

            Location parent = locationRepository.findById(request.parentLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Location", request.parentLocationId().toString()));

            if (!parent.getOrganizationId().equals(organizationId)) {
                throw new DomainViolationException("Invalid Parent Location", "Parent location does not belong to the same organization.");
            }
        }

        location.updateDetails(request.name(), request.address(), request.description());
        location.changeParent(request.parentLocationId());

        Location updated = locationRepository.save(location);
        return LocationResponse.fromDomain(updated);
    }

    @Transactional
    public LocationResponse deactivateLocation(UUID organizationId, UUID locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));

        if (!location.getOrganizationId().equals(organizationId)) {
            throw new ResourceNotFoundException("Location", locationId.toString());
        }

        location.deactivate();
        Location updated = locationRepository.save(location);
        return LocationResponse.fromDomain(updated);
    }

    @Transactional(readOnly = true)
    public List<LocationTreeNodeDto> buildLocationTree(UUID organizationId) {
        List<Location> allLocations = locationRepository.findByOrganizationId(organizationId);

        Map<UUID, LocationTreeNodeDto> nodeMap = new HashMap<>();
        for (Location loc : allLocations) {
            nodeMap.put(loc.getId(), new LocationTreeNodeDto(
                    loc.getId(),
                    loc.getName(),
                    loc.getCode(),
                    loc.getType(),
                    loc.getParentLocationId(),
                    loc.getAddress(),
                    loc.isActive()
            ));
        }

        List<LocationTreeNodeDto> roots = new ArrayList<>();
        for (Location loc : allLocations) {
            LocationTreeNodeDto node = nodeMap.get(loc.getId());
            if (loc.getParentLocationId() == null || !nodeMap.containsKey(loc.getParentLocationId())) {
                roots.add(node);
            } else {
                LocationTreeNodeDto parentNode = nodeMap.get(loc.getParentLocationId());
                parentNode.children().add(node);
            }
        }

        return roots;
    }
}
