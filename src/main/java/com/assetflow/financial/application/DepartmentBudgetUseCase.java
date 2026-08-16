package com.assetflow.financial.application;

import com.assetflow.financial.domain.DepartmentBudget;
import com.assetflow.financial.domain.DepartmentBudgetRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentBudgetUseCase {

    private static final Logger log = LoggerFactory.getLogger(DepartmentBudgetUseCase.class);

    private final DepartmentBudgetRepository budgetRepository;

    public DepartmentBudgetUseCase(DepartmentBudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public DepartmentBudget allocateBudget(
            UUID organizationId,
            UUID departmentId,
            int fiscalYear,
            BigDecimal allocatedAmount,
            String currency,
            String notes) {
        Optional<DepartmentBudget> existing = budgetRepository
                .findByOrganizationIdAndDepartmentIdAndFiscalYear(organizationId, departmentId, fiscalYear);

        if (existing.isPresent()) {
            throw new DomainViolationException(
                    "Budget already exists for department " + departmentId + " and fiscal year " + fiscalYear);
        }

        DepartmentBudget budget = DepartmentBudget.create(
                organizationId,
                departmentId,
                fiscalYear,
                allocatedAmount,
                currency,
                notes
        );

        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Allocated budget {} for dept {} in FY{} of amount {}", saved.getId(), departmentId, fiscalYear, allocatedAmount);
        return saved;
    }

    @Transactional
    public DepartmentBudget adjustAllocation(UUID organizationId, UUID budgetId, BigDecimal additionalAmount) {
        DepartmentBudget budget = budgetRepository.findByOrganizationIdAndId(organizationId, budgetId)
                .orElseThrow(() -> new DomainViolationException("Department budget not found"));

        budget.increaseAllocation(additionalAmount);
        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Adjusted budget {} allocation by +{}", budgetId, additionalAmount);
        return saved;
    }

    @Transactional
    public DepartmentBudget checkAndCommitFunds(UUID organizationId, UUID departmentId, int fiscalYear, BigDecimal amount) {
        DepartmentBudget budget = budgetRepository
                .findByOrganizationIdAndDepartmentIdAndFiscalYear(organizationId, departmentId, fiscalYear)
                .orElseThrow(() -> new DomainViolationException("No active budget found for department in fiscal year " + fiscalYear));

        budget.commitFunds(amount);
        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Committed {} on dept budget {}", amount, saved.getId());
        return saved;
    }

    @Transactional
    public DepartmentBudget recordSpend(UUID organizationId, UUID departmentId, int fiscalYear, BigDecimal amount, boolean fromCommitted) {
        DepartmentBudget budget = budgetRepository
                .findByOrganizationIdAndDepartmentIdAndFiscalYear(organizationId, departmentId, fiscalYear)
                .orElseThrow(() -> new DomainViolationException("No active budget found for department in fiscal year " + fiscalYear));

        budget.recordSpend(amount, fromCommitted);
        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Recorded spend of {} on dept budget {}", amount, saved.getId());
        return saved;
    }

    @Transactional
    public DepartmentBudget freezeBudget(UUID organizationId, UUID budgetId) {
        DepartmentBudget budget = budgetRepository.findByOrganizationIdAndId(organizationId, budgetId)
                .orElseThrow(() -> new DomainViolationException("Department budget not found"));

        budget.freeze();
        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Frozen dept budget {}", budgetId);
        return saved;
    }

    @Transactional
    public DepartmentBudget unfreezeBudget(UUID organizationId, UUID budgetId) {
        DepartmentBudget budget = budgetRepository.findByOrganizationIdAndId(organizationId, budgetId)
                .orElseThrow(() -> new DomainViolationException("Department budget not found"));

        budget.unfreeze();
        DepartmentBudget saved = budgetRepository.save(budget);
        log.info("Unfrozen dept budget {}", budgetId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<DepartmentBudget> getBudgets(UUID organizationId, Integer fiscalYear) {
        if (fiscalYear != null) {
            return budgetRepository.findByOrganizationIdAndFiscalYear(organizationId, fiscalYear);
        }
        return budgetRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<DepartmentBudget> getBudgetForDepartment(UUID organizationId, UUID departmentId, int fiscalYear) {
        return budgetRepository.findByOrganizationIdAndDepartmentIdAndFiscalYear(organizationId, departmentId, fiscalYear);
    }

    @Transactional(readOnly = true)
    public Optional<DepartmentBudget> getBudgetById(UUID organizationId, UUID budgetId) {
        return budgetRepository.findByOrganizationIdAndId(organizationId, budgetId);
    }
}
