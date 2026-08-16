package com.assetflow.financial.application;

import com.assetflow.financial.domain.DepartmentBudget;
import com.assetflow.financial.domain.DepartmentBudgetRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentBudgetUseCaseTest {

    @Mock
    private DepartmentBudgetRepository budgetRepository;

    private DepartmentBudgetUseCase useCase;

    private final UUID orgId = UUID.randomUUID();
    private final UUID deptId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DepartmentBudgetUseCase(budgetRepository);
    }

    @Test
    @DisplayName("Should allocate department budget successfully")
    void shouldAllocateBudget() {
        when(budgetRepository.findByOrganizationIdAndDepartmentIdAndFiscalYear(orgId, deptId, 2026))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(DepartmentBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentBudget budget = useCase.allocateBudget(
                orgId, deptId, 2026, new BigDecimal("75000.00"), "USD", "Engineering"
        );

        assertThat(budget).isNotNull();
        assertThat(budget.getFiscalYear()).isEqualTo(2026);
        assertThat(budget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("75000.00"));
        assertThat(budget.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should prevent duplicate budget allocation for same dept and year")
    void shouldPreventDuplicateAllocation() {
        DepartmentBudget existing = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("50000.00"), "USD", "Existing"
        );
        when(budgetRepository.findByOrganizationIdAndDepartmentIdAndFiscalYear(orgId, deptId, 2026))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.allocateBudget(orgId, deptId, 2026, new BigDecimal("20000.00"), "USD", "Duplicate"))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Budget already exists");
    }

    @Test
    @DisplayName("Should check and commit funds on active budget")
    void shouldCommitFunds() {
        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("60000.00"), "USD", "Ops"
        );
        when(budgetRepository.findByOrganizationIdAndDepartmentIdAndFiscalYear(orgId, deptId, 2026))
                .thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(DepartmentBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentBudget updated = useCase.checkAndCommitFunds(orgId, deptId, 2026, new BigDecimal("10000.00"));

        assertThat(updated.getCommittedAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(updated.getRemainingBudget()).isEqualByComparingTo(new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("Should record spend on budget")
    void shouldRecordSpend() {
        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("40000.00"), "USD", "HR"
        );
        budget.commitFunds(new BigDecimal("8000.00"));

        when(budgetRepository.findByOrganizationIdAndDepartmentIdAndFiscalYear(orgId, deptId, 2026))
                .thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(DepartmentBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentBudget updated = useCase.recordSpend(orgId, deptId, 2026, new BigDecimal("8000.00"), true);

        assertThat(updated.getCommittedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(updated.getSpentAmount()).isEqualByComparingTo(new BigDecimal("8000.00"));
    }

    @Test
    @DisplayName("Should freeze and unfreeze budget")
    void shouldFreezeAndUnfreeze() {
        UUID budgetId = UUID.randomUUID();
        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("30000.00"), "USD", "Support"
        );
        when(budgetRepository.findByOrganizationIdAndId(orgId, budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(DepartmentBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentBudget frozen = useCase.freezeBudget(orgId, budgetId);
        assertThat(frozen.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.FROZEN);

        DepartmentBudget unfrozen = useCase.unfreezeBudget(orgId, budgetId);
        assertThat(unfrozen.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.ACTIVE);
    }
}
