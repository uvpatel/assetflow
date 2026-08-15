package com.assetflow.organization.application;

import com.assetflow.organization.domain.DepartmentHierarchy;
import com.assetflow.organization.domain.DepartmentHierarchyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentHierarchyUseCase {

    private final DepartmentHierarchyRepository repository;

    public DepartmentHierarchyUseCase(DepartmentHierarchyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DepartmentHierarchy setParentDepartment(UUID organizationId, UUID departmentId, UUID parentDepartmentId) {
        if (departmentId.equals(parentDepartmentId)) {
            throw new IllegalArgumentException("Department cannot be its own parent");
        }

        // Cycle check: verify parent is not a descendant of department
        if (parentDepartmentId != null && isDescendant(organizationId, parentDepartmentId, departmentId)) {
            throw new IllegalArgumentException("Circular hierarchy detected between department and parent department");
        }

        DepartmentHierarchy hierarchy = repository.findByDepartmentId(departmentId)
                .orElse(new DepartmentHierarchy(departmentId, organizationId, parentDepartmentId));
        
        hierarchy.updateParentDepartment(parentDepartmentId);
        return repository.save(hierarchy);
    }

    @Transactional(readOnly = true)
    public List<DepartmentHierarchy> getChildDepartments(UUID parentDepartmentId) {
        return repository.findByParentDepartmentId(parentDepartmentId);
    }

    @Transactional(readOnly = true)
    public List<DepartmentHierarchy> getOrganizationHierarchy(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    private boolean isDescendant(UUID organizationId, UUID targetId, UUID ancestorId) {
        UUID current = targetId;
        while (current != null) {
            if (current.equals(ancestorId)) {
                return true;
            }
            UUID parentId = current;
            current = repository.findByDepartmentId(parentId)
                    .map(DepartmentHierarchy::getParentDepartmentId)
                    .orElse(null);
        }
        return false;
    }
}
