package com.assetflow.shared.api;

import com.assetflow.shared.application.DataRetentionPolicyUseCase;
import com.assetflow.shared.domain.DataRetentionPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/retention-policies")
public class DataRetentionPolicyController {

    private final DataRetentionPolicyUseCase useCase;

    public DataRetentionPolicyController(DataRetentionPolicyUseCase useCase) {
        this.useCase = useCase;
    }

    public record ConfigurePolicyRequest(
            String targetModule,
            int retentionDays,
            String action
    ) {}

    public record PolicyResponse(
            UUID id,
            UUID organizationId,
            String targetModule,
            int retentionDays,
            String action,
            boolean isActive,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<PolicyResponse> configurePolicy(
            @PathVariable UUID organizationId,
            @RequestBody ConfigurePolicyRequest request) {
        DataRetentionPolicy policy = useCase.configurePolicy(
                organizationId,
                request.targetModule(),
                request.retentionDays(),
                request.action()
        );
        return ResponseEntity.ok(toResponse(policy));
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getPolicies(@PathVariable UUID organizationId) {
        List<PolicyResponse> list = useCase.getOrganizationPolicies(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private PolicyResponse toResponse(DataRetentionPolicy p) {
        return new PolicyResponse(
                p.getId(),
                p.getOrganizationId(),
                p.getTargetModule(),
                p.getRetentionDays(),
                p.getAction().name(),
                p.isActive(),
                p.getCreatedAt()
        );
    }
}
