package com.assetflow.organization.application;

import com.assetflow.organization.api.dto.DepartmentTreeNodeDto;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentHierarchy;
import com.assetflow.organization.domain.DepartmentHierarchyRepository;
import com.assetflow.organization.domain.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
public class DepartmentHierarchyUseCase {

    private final DepartmentHierarchyRepository repository;
    private final DepartmentRepository departmentRepository;

    public DepartmentHierarchyUseCase(DepartmentHierarchyRepository repository,
                                       DepartmentRepository departmentRepository) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
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

    @Transactional(readOnly = true)
    public List<DepartmentTreeNodeDto> getDepartmentTree(UUID organizationId) {
        List<Department> departments = departmentRepository.findByOrganizationId(organizationId);
        List<DepartmentHierarchy> hierarchies = repository.findByOrganizationId(organizationId);
        Map<UUID, UUID> parentMap = hierarchies.stream()
                .filter(h -> h.getParentDepartmentId() != null)
                .collect(Collectors.toMap(DepartmentHierarchy::getDepartmentId, DepartmentHierarchy::getParentDepartmentId));

        Map<UUID, List<Department>> childrenByParent = new java.util.HashMap<>();
        for (Department dept : departments) {
            UUID parentId = parentMap.get(dept.getId());
            childrenByParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(dept);
        }

        List<Department> rootDepartments = childrenByParent.getOrDefault(null, new ArrayList<>());
        return rootDepartments.stream()
                .map(root -> buildTreeNode(root, childrenByParent))
                .toList();
    }

    private DepartmentTreeNodeDto buildTreeNode(Department dept, Map<UUID, List<Department>> childrenByParent) {
        List<Department> children = childrenByParent.getOrDefault(dept.getId(), new ArrayList<>());
        List<DepartmentTreeNodeDto> childNodes = children.stream()
                .map(child -> buildTreeNode(child, childrenByParent))
                .toList();

        return new DepartmentTreeNodeDto(
                dept.getId(),
                dept.getName(),
                dept.getCode(),
                null,
                childNodes
        );
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
