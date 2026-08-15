package com.assetflow.shared.api;

import com.assetflow.shared.application.DataRetentionPolicyUseCase;
import com.assetflow.shared.domain.DataRetentionExecutionLog;
import com.assetflow.shared.domain.DataRetentionPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            Instant lastExecutedAt,
            int recordsProcessed,
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

    @PostMapping("/{policyId}/execute")
    public ResponseEntity<DataRetentionExecutionLog> executePolicy(
            @PathVariable UUID organizationId,
            @PathVariable UUID policyId) {
        DataRetentionExecutionLog log = useCase.executeRetentionPolicy(organizationId, policyId);
        return ResponseEntity.ok(log);
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
                p.getLastExecutedAt(),
                p.getRecordsProcessed(),
                p.getCreatedAt()
        );
    }
}
