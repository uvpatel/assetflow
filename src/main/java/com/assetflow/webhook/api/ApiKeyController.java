package com.assetflow.webhook.api;

import com.assetflow.webhook.application.ApiKeyUseCase;
import com.assetflow.webhook.domain.ApiKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/api-keys")
public class ApiKeyController {

    private final ApiKeyUseCase useCase;

    public ApiKeyController(ApiKeyUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateApiKeyRequest(
            String name,
            String scopes,
            Instant expiresAt
    ) {}

    public record ApiKeyCreatedResponse(
            UUID id,
            UUID organizationId,
            String name,
            String keyPrefix,
            String plainTextSecret,
            String scopes,
            Instant expiresAt
    ) {}

    public record ApiKeySummaryResponse(
            UUID id,
            UUID organizationId,
            String name,
            String keyPrefix,
            String scopes,
            Instant expiresAt,
            boolean isRevoked,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<ApiKeyCreatedResponse> createApiKey(
            @PathVariable UUID organizationId,
            @RequestBody CreateApiKeyRequest request) {
        ApiKeyUseCase.GeneratedApiKey result = useCase.createApiKey(
                organizationId,
                request.name(),
                request.scopes(),
                request.expiresAt()
        );
        ApiKey k = result.apiKey();
        return ResponseEntity.ok(new ApiKeyCreatedResponse(
                k.getId(),
                k.getOrganizationId(),
                k.getName(),
                k.getKeyPrefix(),
                result.plainTextSecret(),
                k.getScopes(),
                k.getExpiresAt()
        ));
    }

    @PostMapping("/{apiKeyId}/revoke")
    public ResponseEntity<Void> revokeApiKey(@PathVariable UUID apiKeyId) {
        useCase.revokeApiKey(apiKeyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ApiKeySummaryResponse>> getApiKeys(@PathVariable UUID organizationId) {
        List<ApiKeySummaryResponse> list = useCase.getApiKeys(organizationId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private ApiKeySummaryResponse toSummaryResponse(ApiKey k) {
        return new ApiKeySummaryResponse(
                k.getId(),
                k.getOrganizationId(),
                k.getName(),
                k.getKeyPrefix(),
                k.getScopes(),
                k.getExpiresAt(),
                k.isRevoked(),
                k.getCreatedAt()
        );
    }
}
