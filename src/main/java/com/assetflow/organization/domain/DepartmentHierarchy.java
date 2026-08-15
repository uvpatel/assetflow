package com.assetflow.organization.domain;

import java.util.Objects;
import java.util.UUID;

public class DepartmentHierarchy {
    private final UUID departmentId;
    private final UUID organizationId;
    private UUID parentDepartmentId;

    public DepartmentHierarchy(UUID departmentId, UUID organizationId, UUID parentDepartmentId) {
        if (Objects.equals(departmentId, parentDepartmentId)) {
            throw new IllegalArgumentException("A department cannot be its own parent");
        }
        this.departmentId = departmentId;
        this.organizationId = organizationId;
        this.parentDepartmentId = parentDepartmentId;
    }

    public void updateParentDepartment(UUID newParentId) {
        if (Objects.equals(departmentId, newParentId)) {
            throw new IllegalArgumentException("A department cannot be its own parent");
        }
        this.parentDepartmentId = newParentId;
    }

    public UUID getDepartmentId() { return departmentId; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getParentDepartmentId() { return parentDepartmentId; }
}
