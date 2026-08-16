package com.assetflow.organization.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {

    @Test
    void shouldCreateLocationAggregate() {
        UUID orgId = UUID.randomUUID();
        Location loc = Location.create(orgId, "HQ Building A", "HQ-BLD-A", LocationType.BUILDING, null, "123 Tech Blvd", "Main Office");

        assertThat(loc.getId()).isNotNull();
        assertThat(loc.getOrganizationId()).isEqualTo(orgId);
        assertThat(loc.getName()).isEqualTo("HQ Building A");
        assertThat(loc.getCode()).isEqualTo("HQ-BLD-A");
        assertThat(loc.getType()).isEqualTo(LocationType.BUILDING);
        assertThat(loc.getParentLocationId()).isNull();
        assertThat(loc.getAddress()).isEqualTo("123 Tech Blvd");
        assertThat(loc.isActive()).isTrue();
    }

    @Test
    void shouldUpdateLocationDetails() {
        Location loc = Location.create(UUID.randomUUID(), "Building A", "BLD-A", LocationType.BUILDING, null, null, null);

        loc.updateDetails("Building A - Renovated", "456 New St", "Updated notes");

        assertThat(loc.getName()).isEqualTo("Building A - Renovated");
        assertThat(loc.getAddress()).isEqualTo("456 New St");
        assertThat(loc.getDescription()).isEqualTo("Updated notes");
    }

    @Test
    void shouldPreventSelfParentAssignment() {
        Location loc = Location.create(UUID.randomUUID(), "Building A", "BLD-A", LocationType.BUILDING, null, null, null);

        assertThatThrownBy(() -> loc.changeParent(loc.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A location cannot be its own parent");
    }

    @Test
    void shouldDeactivateAndReactivateLocation() {
        Location loc = Location.create(UUID.randomUUID(), "Server Room 1", "SR-01", LocationType.ROOM, null, null, null);

        assertThat(loc.isActive()).isTrue();

        loc.deactivate();
        assertThat(loc.isActive()).isFalse();

        loc.activate();
        assertThat(loc.isActive()).isTrue();
    }
}
