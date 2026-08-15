package com.assetflow.shared.api;

import com.assetflow.shared.application.FeatureFlagUseCase;
import com.assetflow.shared.domain.FeatureFlag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/feature-flags")
public class FeatureFlagController {

    private final FeatureFlagUseCase useCase;

    public FeatureFlagController(FeatureFlagUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateFlagRequest(
            String flagKey,
            String description,
            boolean isEnabled
    ) {}

    public record ToggleFlagRequest(boolean isEnabled) {}

    public record FeatureFlagResponse(
            UUID id,
            UUID organizationId,
            String flagKey,
            String description,
            boolean isEnabled,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> createFlag(
            @PathVariable UUID organizationId,
            @RequestBody CreateFlagRequest request) {
        FeatureFlag flag = useCase.createFlag(
                organizationId,
                request.flagKey(),
                request.description(),
                request.isEnabled()
        );
        return ResponseEntity.ok(toResponse(flag));
    }

    @PutMapping("/{flagKey}/toggle")
    public ResponseEntity<FeatureFlagResponse> toggleFlag(
            @PathVariable UUID organizationId,
            @PathVariable String flagKey,
            @RequestBody ToggleFlagRequest request) {
        FeatureFlag flag = useCase.toggleFlag(organizationId, flagKey, request.isEnabled());
        return ResponseEntity.ok(toResponse(flag));
    }

    @GetMapping("/{flagKey}/evaluate")
    public ResponseEntity<Boolean> evaluateFlag(
            @PathVariable UUID organizationId,
            @PathVariable String flagKey,
            @RequestParam(defaultValue = "false") boolean defaultValue) {
        boolean enabled = useCase.isFeatureEnabled(organizationId, flagKey, defaultValue);
        return ResponseEntity.ok(enabled);
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getFlags(@PathVariable UUID organizationId) {
        List<FeatureFlagResponse> list = useCase.getFlagsForOrganization(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private FeatureFlagResponse toResponse(FeatureFlag f) {
        return new FeatureFlagResponse(
                f.getId(),
                f.getOrganizationId(),
                f.getFlagKey(),
                f.getDescription(),
                f.isEnabled(),
                f.getCreatedAt()
        );
    }
}
