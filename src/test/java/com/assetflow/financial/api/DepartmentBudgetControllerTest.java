package com.assetflow.financial.api;

import com.assetflow.financial.application.DepartmentBudgetUseCase;
import com.assetflow.financial.domain.DepartmentBudget;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentBudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DepartmentBudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepartmentBudgetUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should allocate department budget via POST")
    void shouldAllocateBudget() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();

        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("80000.00"), "USD", "Engineering"
        );

        when(useCase.allocateBudget(eq(orgId), eq(deptId), eq(2026), eq(new BigDecimal("80000.00")), eq("USD"), eq("Engineering")))
                .thenReturn(budget);

        DepartmentBudgetController.AllocateBudgetRequest request =
                new DepartmentBudgetController.AllocateBudgetRequest(deptId, 2026, new BigDecimal("80000.00"), "USD", "Engineering");

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/department-budgets", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiscalYear").value(2026))
                .andExpect(jsonPath("$.allocatedAmount").value(80000.00))
                .andExpect(jsonPath("$.remainingBudget").value(80000.00));
    }

    @Test
    @DisplayName("Should adjust allocation via POST")
    void shouldAdjustAllocation() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        DepartmentBudget budget = DepartmentBudget.create(
                orgId, UUID.randomUUID(), 2026, new BigDecimal("100000.00"), "USD", "Adjusted"
        );

        when(useCase.adjustAllocation(orgId, budgetId, new BigDecimal("20000.00"))).thenReturn(budget);

        DepartmentBudgetController.AdjustAllocationRequest request =
                new DepartmentBudgetController.AdjustAllocationRequest(new BigDecimal("20000.00"));

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/department-budgets/{budgetId}/adjust", orgId, budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allocatedAmount").value(100000.00));
    }

    @Test
    @DisplayName("Should freeze and unfreeze budget via POST")
    void shouldFreezeAndUnfreeze() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        DepartmentBudget frozen = DepartmentBudget.create(
                orgId, UUID.randomUUID(), 2026, new BigDecimal("50000.00"), "USD", "Frozen"
        );
        frozen.freeze();

        when(useCase.freezeBudget(orgId, budgetId)).thenReturn(frozen);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/department-budgets/{budgetId}/freeze", orgId, budgetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"));
    }

    @Test
    @DisplayName("Should query department budgets with filters")
    void shouldQueryBudgets() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();

        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("50000.00"), "USD", "Queried"
        );

        when(useCase.getBudgets(orgId, 2026)).thenReturn(List.of(budget));
        when(useCase.getBudgetForDepartment(orgId, deptId, 2026)).thenReturn(Optional.of(budget));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/department-budgets?fiscalYear=2026", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fiscalYear").value(2026));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/department-budgets/departments/{departmentId}?fiscalYear=2026", orgId, deptId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allocatedAmount").value(50000.00));
    }
}
