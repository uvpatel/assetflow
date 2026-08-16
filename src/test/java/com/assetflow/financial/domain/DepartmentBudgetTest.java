package com.assetflow.financial.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepartmentBudgetTest {

    @Test
    @DisplayName("Should create department budget and calculate remaining amount")
    void shouldCreateDepartmentBudget() {
        UUID orgId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();

        DepartmentBudget budget = DepartmentBudget.create(
                orgId, deptId, 2026, new BigDecimal("100000.00"), "USD", "Engineering FY2026"
        );

        assertThat(budget.getOrganizationId()).isEqualTo(orgId);
        assertThat(budget.getDepartmentId()).isEqualTo(deptId);
        assertThat(budget.getFiscalYear()).isEqualTo(2026);
        assertThat(budget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(budget.getCommittedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(budget.getSpentAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(budget.getRemainingBudget()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(budget.hasSufficientBudget(new BigDecimal("50000.00"))).isTrue();
    }

    @Test
    @DisplayName("Should commit funds and update remaining budget")
    void shouldCommitFunds() {
        DepartmentBudget budget = DepartmentBudget.create(
                UUID.randomUUID(), UUID.randomUUID(), 2026, new BigDecimal("50000.00"), "USD", "IT Ops"
        );

        budget.commitFunds(new BigDecimal("15000.00"));

        assertThat(budget.getCommittedAmount()).isEqualByComparingTo(new BigDecimal("15000.00"));
        assertThat(budget.getRemainingBudget()).isEqualByComparingTo(new BigDecimal("35000.00"));
    }

    @Test
    @DisplayName("Should throw when committing funds exceeding remaining budget")
    void shouldThrowOnOvercommit() {
        DepartmentBudget budget = DepartmentBudget.create(
                UUID.randomUUID(), UUID.randomUUID(), 2026, new BigDecimal("10000.00"), "USD", "Facilities"
        );

        assertThatThrownBy(() -> budget.commitFunds(new BigDecimal("15000.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient budget");
    }

    @Test
    @DisplayName("Should record spend and release committed funds")
    void shouldRecordSpend() {
        DepartmentBudget budget = DepartmentBudget.create(
                UUID.randomUUID(), UUID.randomUUID(), 2026, new BigDecimal("20000.00"), "USD", "Marketing"
        );

        budget.commitFunds(new BigDecimal("5000.00"));
        budget.recordSpend(new BigDecimal("5000.00"), true);

        assertThat(budget.getCommittedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(budget.getSpentAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(budget.getRemainingBudget()).isEqualByComparingTo(new BigDecimal("15000.00"));
    }

    @Test
    @DisplayName("Should handle freeze, unfreeze, and allocation adjustment")
    void shouldHandleLifecycle() {
        DepartmentBudget budget = DepartmentBudget.create(
                UUID.randomUUID(), UUID.randomUUID(), 2026, new BigDecimal("20000.00"), "USD", "Legal"
        );

        budget.increaseAllocation(new BigDecimal("5000.00"));
        assertThat(budget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("25000.00"));

        budget.freeze();
        assertThat(budget.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.FROZEN);

        assertThatThrownBy(() -> budget.commitFunds(new BigDecimal("1000.00")))
                .isInstanceOf(IllegalStateException.class);

        budget.unfreeze();
        assertThat(budget.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.ACTIVE);

        budget.close();
        assertThat(budget.getStatus()).isEqualTo(DepartmentBudget.BudgetStatus.CLOSED);
    }
}
