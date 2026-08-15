package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.MaintenanceSlaUseCase;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/maintenance-sla")
public class MaintenanceSlaController {

    private final MaintenanceSlaUseCase useCase;

    public MaintenanceSlaController(MaintenanceSlaUseCase useCase) {
        this.useCase = useCase;
    }

    public record ConfigureSlaRequest(
            String priority,
            int responseDeadlineHours,
            int resolutionDeadlineHours,
            String escalationEmail
    ) {}

    public record SlaPolicyResponse(
            UUID id,
            UUID organizationId,
            String priority,
            int responseDeadlineHours,
            int resolutionDeadlineHours,
            String escalationEmail
    ) {}

    @PostMapping
    public ResponseEntity<SlaPolicyResponse> configureSla(
            @PathVariable UUID organizationId,
            @RequestBody ConfigureSlaRequest request) {
        MaintenanceSlaPolicy policy = useCase.configureSlaPolicy(
                organizationId,
                request.priority(),
                request.responseDeadlineHours(),
                request.resolutionDeadlineHours(),
                request.escalationEmail()
        );
        return ResponseEntity.ok(toResponse(policy));
    }

    @GetMapping
    public ResponseEntity<List<SlaPolicyResponse>> getSlaPolicies(@PathVariable UUID organizationId) {
        List<SlaPolicyResponse> list = useCase.getSlaPolicies(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private SlaPolicyResponse toResponse(MaintenanceSlaPolicy p) {
        return new SlaPolicyResponse(
                p.getId(),
                p.getOrganizationId(),
                p.getPriority().name(),
                p.getResponseDeadlineHours(),
                p.getResolutionDeadlineHours(),
                p.getEscalationEmail()
        );
    }
}
