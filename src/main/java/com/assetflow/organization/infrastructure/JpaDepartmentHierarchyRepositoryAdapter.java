package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentHierarchy;
import com.assetflow.organization.domain.DepartmentHierarchyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaDepartmentHierarchyRepositoryAdapter implements DepartmentHierarchyRepository {

    private final SpringDataDepartmentRepository departmentRepo;

    public JpaDepartmentHierarchyRepositoryAdapter(SpringDataDepartmentRepository departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    @Override
    public DepartmentHierarchy save(DepartmentHierarchy hierarchy) {
        Optional<Department> deptOpt = departmentRepo.findById(hierarchy.getDepartmentId());
        if (deptOpt.isPresent()) {
            Department dept = deptOpt.get();
            dept.setParentDepartmentId(hierarchy.getParentDepartmentId());
            departmentRepo.save(dept);
        }
        return hierarchy;
    }

    @Override
    public Optional<DepartmentHierarchy> findByDepartmentId(UUID departmentId) {
        return departmentRepo.findById(departmentId)
                .map(d -> new DepartmentHierarchy(d.getId(), d.getOrganizationId(), d.getParentDepartmentId()));
    }

    @Override
    public List<DepartmentHierarchy> findByParentDepartmentId(UUID parentDepartmentId) {
        return departmentRepo.findAll().stream()
                .filter(d -> parentDepartmentId.equals(d.getParentDepartmentId()))
                .map(d -> new DepartmentHierarchy(d.getId(), d.getOrganizationId(), d.getParentDepartmentId()))
                .toList();
    }

    @Override
    public List<DepartmentHierarchy> findByOrganizationId(UUID organizationId) {
        return departmentRepo.findByOrganizationId(organizationId).stream()
                .map(d -> new DepartmentHierarchy(d.getId(), d.getOrganizationId(), d.getParentDepartmentId()))
                .toList();
    }
}
