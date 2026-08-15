package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.MaintenanceDispatchUseCase;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceDispatchController {

    private final MaintenanceDispatchUseCase dispatchUseCase;

    public MaintenanceDispatchController(MaintenanceDispatchUseCase dispatchUseCase) {
        this.dispatchUseCase = dispatchUseCase;
    }

    @PostMapping("/requests/{requestId}/dispatch")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MaintenanceRequest> dispatchWorkOrder(
            @PathVariable UUID requestId,
            @RequestParam(required = false) UUID technicianId) {
        MaintenanceRequest dispatched = dispatchUseCase.dispatchWorkOrder(requestId, technicianId);
        return ResponseEntity.ok(dispatched);
    }

    @PostMapping("/sla/evaluate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<Map<String, Integer>> evaluateSlaStatuses() {
        int breachedCount = dispatchUseCase.evaluateSlaStatuses();
        return ResponseEntity.ok(Map.of("breachedCount", breachedCount));
    }
}
