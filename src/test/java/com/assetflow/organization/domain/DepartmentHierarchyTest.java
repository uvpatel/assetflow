package com.assetflow.organization.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepartmentHierarchyTest {

    @Test
    void shouldCreateValidDepartmentHierarchy() {
        UUID deptId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        DepartmentHierarchy hierarchy = new DepartmentHierarchy(deptId, orgId, parentId);

        assertThat(hierarchy.getDepartmentId()).isEqualTo(deptId);
        assertThat(hierarchy.getOrganizationId()).isEqualTo(orgId);
        assertThat(hierarchy.getParentDepartmentId()).isEqualTo(parentId);
    }

    @Test
    void shouldRejectSelfAsParent() {
        UUID deptId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> new DepartmentHierarchy(deptId, orgId, deptId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be its own parent");
    }
}
