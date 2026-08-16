package com.assetflow.financial.api;

import com.assetflow.financial.application.DepartmentBudgetUseCase;
import com.assetflow.financial.domain.DepartmentBudget;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/department-budgets")
public class DepartmentBudgetController {

    private final DepartmentBudgetUseCase useCase;

    public DepartmentBudgetController(DepartmentBudgetUseCase useCase) {
        this.useCase = useCase;
    }

    public record AllocateBudgetRequest(
            UUID departmentId,
            int fiscalYear,
            BigDecimal allocatedAmount,
            String currency,
            String notes
    ) {}

    public record AdjustAllocationRequest(
            BigDecimal additionalAmount
    ) {}

    public record DepartmentBudgetResponse(
            UUID id,
            UUID organizationId,
            UUID departmentId,
            int fiscalYear,
            BigDecimal allocatedAmount,
            BigDecimal committedAmount,
            BigDecimal spentAmount,
            BigDecimal remainingBudget,
            String currency,
            String status,
            String notes,
            Instant createdAt,
            Instant updatedAt
    ) {}

    @PostMapping
    public ResponseEntity<DepartmentBudgetResponse> allocateBudget(
            @PathVariable UUID organizationId,
            @RequestBody AllocateBudgetRequest request) {
        DepartmentBudget budget = useCase.allocateBudget(
                organizationId,
                request.departmentId(),
                request.fiscalYear(),
                request.allocatedAmount(),
                request.currency(),
                request.notes()
        );
        return ResponseEntity.ok(toResponse(budget));
    }

    @PostMapping("/{budgetId}/adjust")
    public ResponseEntity<DepartmentBudgetResponse> adjustAllocation(
            @PathVariable UUID organizationId,
            @PathVariable UUID budgetId,
            @RequestBody AdjustAllocationRequest request) {
        DepartmentBudget budget = useCase.adjustAllocation(organizationId, budgetId, request.additionalAmount());
        return ResponseEntity.ok(toResponse(budget));
    }

    @PostMapping("/{budgetId}/freeze")
    public ResponseEntity<DepartmentBudgetResponse> freezeBudget(
            @PathVariable UUID organizationId,
            @PathVariable UUID budgetId) {
        DepartmentBudget budget = useCase.freezeBudget(organizationId, budgetId);
        return ResponseEntity.ok(toResponse(budget));
    }

    @PostMapping("/{budgetId}/unfreeze")
    public ResponseEntity<DepartmentBudgetResponse> unfreezeBudget(
            @PathVariable UUID organizationId,
            @PathVariable UUID budgetId) {
        DepartmentBudget budget = useCase.unfreezeBudget(organizationId, budgetId);
        return ResponseEntity.ok(toResponse(budget));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentBudgetResponse>> getBudgets(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) Integer fiscalYear) {
        List<DepartmentBudgetResponse> list = useCase.getBudgets(organizationId, fiscalYear)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<DepartmentBudgetResponse> getBudgetForDepartment(
            @PathVariable UUID organizationId,
            @PathVariable UUID departmentId,
            @RequestParam(defaultValue = "2026") int fiscalYear) {
        return useCase.getBudgetForDepartment(organizationId, departmentId, fiscalYear)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<DepartmentBudgetResponse> getBudgetById(
            @PathVariable UUID organizationId,
            @PathVariable UUID budgetId) {
        return useCase.getBudgetById(organizationId, budgetId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DepartmentBudgetResponse toResponse(DepartmentBudget b) {
        return new DepartmentBudgetResponse(
                b.getId(),
                b.getOrganizationId(),
                b.getDepartmentId(),
                b.getFiscalYear(),
                b.getAllocatedAmount(),
                b.getCommittedAmount(),
                b.getSpentAmount(),
                b.getRemainingBudget(),
                b.getCurrency(),
                b.getStatus().name(),
                b.getNotes(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
