package com.assetflow.organization.api;

import com.assetflow.organization.application.DepartmentHierarchyUseCase;
import com.assetflow.organization.domain.DepartmentHierarchy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/departments")
public class DepartmentHierarchyController {

    private final DepartmentHierarchyUseCase useCase;

    public DepartmentHierarchyController(DepartmentHierarchyUseCase useCase) {
        this.useCase = useCase;
    }

    public record SetParentDepartmentRequest(UUID parentDepartmentId) {}

    public record DepartmentHierarchyResponse(
            UUID departmentId,
            UUID organizationId,
            UUID parentDepartmentId
    ) {}

    @PutMapping("/{departmentId}/parent")
    public ResponseEntity<DepartmentHierarchyResponse> setParent(
            @PathVariable UUID organizationId,
            @PathVariable UUID departmentId,
            @RequestBody SetParentDepartmentRequest request) {
        DepartmentHierarchy updated = useCase.setParentDepartment(organizationId, departmentId, request.parentDepartmentId());
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/{departmentId}/children")
    public ResponseEntity<List<DepartmentHierarchyResponse>> getChildren(@PathVariable UUID departmentId) {
        List<DepartmentHierarchyResponse> children = useCase.getChildDepartments(departmentId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(children);
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<List<DepartmentHierarchyResponse>> getHierarchy(@PathVariable UUID organizationId) {
        List<DepartmentHierarchyResponse> hierarchy = useCase.getOrganizationHierarchy(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(hierarchy);
    }

    private DepartmentHierarchyResponse toResponse(DepartmentHierarchy h) {
        return new DepartmentHierarchyResponse(h.getDepartmentId(), h.getOrganizationId(), h.getParentDepartmentId());
    }
}
