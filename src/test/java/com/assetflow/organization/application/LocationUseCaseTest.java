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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationUseCaseTest {

    @Mock
    private LocationRepository locationRepository;

    private LocationUseCase locationUseCase;
    private UUID orgId;

    @BeforeEach
    void setUp() {
        locationUseCase = new LocationUseCase(locationRepository);
        orgId = UUID.randomUUID();
    }

    @Test
    void shouldCreateRootLocationSuccessfully() {
        CreateLocationRequest request = new CreateLocationRequest("Main Campus", "CAMPUS-01", LocationType.CAMPUS, null, "100 Silicon Way", "Headquarters");

        when(locationRepository.existsByOrganizationIdAndCode(orgId, "CAMPUS-01")).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationResponse response = locationUseCase.createLocation(orgId, request);

        assertThat(response.name()).isEqualTo("Main Campus");
        assertThat(response.code()).isEqualTo("CAMPUS-01");
        assertThat(response.type()).isEqualTo(LocationType.CAMPUS);
        assertThat(response.parentLocationId()).isNull();
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void shouldCreateChildLocationWithValidParent() {
        UUID parentId = UUID.randomUUID();
        Location parent = Location.create(orgId, "Main Campus", "CAMPUS-01", LocationType.CAMPUS, null, null, null);

        CreateLocationRequest request = new CreateLocationRequest("Building 1", "BLD-01", LocationType.BUILDING, parentId, null, null);

        when(locationRepository.existsByOrganizationIdAndCode(orgId, "BLD-01")).thenReturn(false);
        when(locationRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationResponse response = locationUseCase.createLocation(orgId, request);

        assertThat(response.parentLocationId()).isEqualTo(parentId);
    }

    @Test
    void shouldRejectDuplicateLocationCode() {
        CreateLocationRequest request = new CreateLocationRequest("Main Campus", "CAMPUS-01", LocationType.CAMPUS, null, null, null);

        when(locationRepository.existsByOrganizationIdAndCode(orgId, "CAMPUS-01")).thenReturn(true);

        assertThatThrownBy(() -> locationUseCase.createLocation(orgId, request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldRejectCrossTenantParentLocation() {
        UUID parentId = UUID.randomUUID();
        Location otherTenantParent = Location.create(UUID.randomUUID(), "Other Campus", "OTHER", LocationType.CAMPUS, null, null, null);

        CreateLocationRequest request = new CreateLocationRequest("Building 1", "BLD-01", LocationType.BUILDING, parentId, null, null);

        when(locationRepository.existsByOrganizationIdAndCode(orgId, "BLD-01")).thenReturn(false);
        when(locationRepository.findById(parentId)).thenReturn(Optional.of(otherTenantParent));

        assertThatThrownBy(() -> locationUseCase.createLocation(orgId, request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("does not belong to the same organization");
    }

    @Test
    void shouldGetLocationById() {
        UUID locId = UUID.randomUUID();
        Location loc = Location.create(orgId, "Server Room", "SR-01", LocationType.ROOM, null, null, null);

        when(locationRepository.findById(locId)).thenReturn(Optional.of(loc));

        LocationResponse response = locationUseCase.getLocationById(orgId, locId);
        assertThat(response.code()).isEqualTo("SR-01");
    }

    @Test
    void shouldBuildLocationTreeHierarchy() {
        Location campus = Location.create(orgId, "Campus", "CAMPUS", LocationType.CAMPUS, null, null, null);
        Location bld = Location.create(orgId, "Building", "BLD", LocationType.BUILDING, campus.getId(), null, null);
        Location room = Location.create(orgId, "Room 101", "RM-101", LocationType.ROOM, bld.getId(), null, null);

        when(locationRepository.findByOrganizationId(orgId)).thenReturn(List.of(campus, bld, room));

        List<LocationTreeNodeDto> tree = locationUseCase.buildLocationTree(orgId);

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).code()).isEqualTo("CAMPUS");
        assertThat(tree.get(0).children()).hasSize(1);
        assertThat(tree.get(0).children().get(0).code()).isEqualTo("BLD");
        assertThat(tree.get(0).children().get(0).children()).hasSize(1);
        assertThat(tree.get(0).children().get(0).children().get(0).code()).isEqualTo("RM-101");
    }

    @Test
    void shouldUpdateLocation() {
        UUID locId = UUID.randomUUID();
        Location loc = Location.create(orgId, "Old Name", "BLD-01", LocationType.BUILDING, null, null, null);

        when(locationRepository.findById(locId)).thenReturn(Optional.of(loc));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateLocationRequest request = new UpdateLocationRequest("New Name", null, "New Address", "New Desc");
        LocationResponse response = locationUseCase.updateLocation(orgId, locId, request);

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.address()).isEqualTo("New Address");
    }

    @Test
    void shouldDeactivateLocation() {
        UUID locId = UUID.randomUUID();
        Location loc = Location.create(orgId, "Room", "RM", LocationType.ROOM, null, null, null);

        when(locationRepository.findById(locId)).thenReturn(Optional.of(loc));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationResponse response = locationUseCase.deactivateLocation(orgId, locId);
        assertThat(response.active()).isFalse();
    }
}
