package com.assetflow.organization.api.dto;

import java.util.List;
import java.util.UUID;

public record DepartmentTreeNodeDto(
        UUID id,
        String name,
        String code,
        UUID parentDepartmentId,
        List<DepartmentTreeNodeDto> children
) {}
