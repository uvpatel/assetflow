package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.TechnicianWorkloadUseCase;
import com.assetflow.maintenance.domain.TechnicianWorkload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/technicians")
public class TechnicianWorkloadController {

    private final TechnicianWorkloadUseCase useCase;

    public TechnicianWorkloadController(TechnicianWorkloadUseCase useCase) {
        this.useCase = useCase;
    }

    public record RegisterTechnicianRequest(
            UUID technicianEmployeeId,
            int maxTaskCapacity,
            String specialization
    ) {}

    public record WorkloadResponse(
            UUID id,
            UUID organizationId,
            UUID technicianEmployeeId,
            int activeTaskCount,
            int maxTaskCapacity,
            String specialization,
            boolean isAvailable
    ) {}

    @PostMapping
    public ResponseEntity<WorkloadResponse> registerTechnician(
            @PathVariable UUID organizationId,
            @RequestBody RegisterTechnicianRequest request) {
        TechnicianWorkload workload = useCase.registerTechnician(
                organizationId,
                request.technicianEmployeeId(),
                request.maxTaskCapacity(),
                request.specialization()
        );
        return ResponseEntity.ok(toResponse(workload));
    }

    @PostMapping("/{technicianEmployeeId}/assign")
    public ResponseEntity<WorkloadResponse> assignTask(
            @PathVariable UUID organizationId,
            @PathVariable UUID technicianEmployeeId) {
        TechnicianWorkload workload = useCase.assignTaskToTechnician(organizationId, technicianEmployeeId);
        return ResponseEntity.ok(toResponse(workload));
    }

    @PostMapping("/{technicianEmployeeId}/complete")
    public ResponseEntity<WorkloadResponse> completeTask(
            @PathVariable UUID organizationId,
            @PathVariable UUID technicianEmployeeId) {
        TechnicianWorkload workload = useCase.completeTaskForTechnician(organizationId, technicianEmployeeId);
        return ResponseEntity.ok(toResponse(workload));
    }

    @GetMapping("/available")
    public ResponseEntity<List<WorkloadResponse>> getAvailableTechnicians(@PathVariable UUID organizationId) {
        List<WorkloadResponse> list = useCase.getAvailableTechnicians(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private WorkloadResponse toResponse(TechnicianWorkload t) {
        return new WorkloadResponse(
                t.getId(),
                t.getOrganizationId(),
                t.getTechnicianEmployeeId(),
                t.getActiveTaskCount(),
                t.getMaxTaskCapacity(),
                t.getSpecialization(),
                t.isAvailable()
        );
    }
}
