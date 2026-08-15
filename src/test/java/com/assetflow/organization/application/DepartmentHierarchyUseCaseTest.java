package com.assetflow.organization.application;

import com.assetflow.organization.api.dto.DepartmentTreeNodeDto;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentHierarchy;
import com.assetflow.organization.domain.DepartmentHierarchyRepository;
import com.assetflow.organization.domain.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentHierarchyUseCaseTest {

    @Mock
    private DepartmentHierarchyRepository hierarchyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private DepartmentHierarchyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DepartmentHierarchyUseCase(hierarchyRepository, departmentRepository);
    }

    @Test
    @DisplayName("Should build nested department tree hierarchy correctly")
    void shouldBuildDepartmentTree() {
        UUID orgId = UUID.randomUUID();
        UUID rootId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();

        Department rootDept = new Department(orgId, "Executive", "EXEC", null);
        rootDept.setId(rootId);
        Department childDept = new Department(orgId, "Engineering", "ENG", null);
        childDept.setId(childId);

        DepartmentHierarchy childHierarchy = new DepartmentHierarchy(childId, orgId, rootId);

        when(departmentRepository.findByOrganizationId(orgId)).thenReturn(List.of(rootDept, childDept));
        when(hierarchyRepository.findByOrganizationId(orgId)).thenReturn(List.of(childHierarchy));

        List<DepartmentTreeNodeDto> tree = useCase.getDepartmentTree(orgId);

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).id()).isEqualTo(rootId);
        assertThat(tree.get(0).children()).hasSize(1);
        assertThat(tree.get(0).children().get(0).id()).isEqualTo(childId);
    }

    @Test
    @DisplayName("Should prevent circular department hierarchy references")
    void shouldPreventCircularHierarchy() {
        UUID orgId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();

        DepartmentHierarchy childHierarchy = new DepartmentHierarchy(childId, orgId, parentId);
        when(hierarchyRepository.findByDepartmentId(childId)).thenReturn(Optional.of(childHierarchy));

        assertThatThrownBy(() -> useCase.setParentDepartment(orgId, parentId, childId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Circular hierarchy detected");
    }
}
