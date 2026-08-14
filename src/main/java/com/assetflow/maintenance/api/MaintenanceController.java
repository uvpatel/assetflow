package com.assetflow.maintenance.api;

import com.assetflow.maintenance.api.dto.CreateMaintenanceRequestDto;
import com.assetflow.maintenance.application.MaintenanceUseCase;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceUseCase maintenanceUseCase;

    public MaintenanceController(MaintenanceUseCase maintenanceUseCase) {
        this.maintenanceUseCase = maintenanceUseCase;
    }

    @PostMapping
    public ResponseEntity<MaintenanceRequest> requestMaintenance(@Valid @RequestBody CreateMaintenanceRequestDto requestDto) {
        MaintenanceRequest request = maintenanceUseCase.requestMaintenance(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<MaintenanceRequest> approveRequest(
            @PathVariable UUID requestId,
            @RequestParam UUID technicianId) {
        MaintenanceRequest request = maintenanceUseCase.approveRequest(requestId, technicianId);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/{requestId}/resolve")
    public ResponseEntity<MaintenanceRequest> resolveRequest(
            @PathVariable UUID requestId,
            @RequestParam(defaultValue = "Resolved successfully") String workNotes) {
        MaintenanceRequest request = maintenanceUseCase.resolveRequest(requestId, workNotes);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRequest>> listRequests() {
        return ResponseEntity.ok(maintenanceUseCase.listRequests());
    }
}
