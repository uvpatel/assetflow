package com.assetflow.reporting.api;

import com.assetflow.reporting.application.SavedSearchPresetUseCase;
import com.assetflow.reporting.domain.SavedSearchPreset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/search-presets")
public class SavedSearchPresetController {

    private final SavedSearchPresetUseCase useCase;

    public SavedSearchPresetController(SavedSearchPresetUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreatePresetRequest(
            UUID userId,
            String name,
            String targetEntity,
            String filterJson,
            boolean isShared
    ) {}

    public record UpdatePresetRequest(
            String name,
            String filterJson,
            boolean isShared
    ) {}

    public record PresetResponse(
            UUID id,
            UUID organizationId,
            UUID userId,
            String name,
            String targetEntity,
            String filterJson,
            boolean isShared,
            boolean isDefault,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<PresetResponse> createPreset(
            @PathVariable UUID organizationId,
            @RequestBody CreatePresetRequest request) {
        SavedSearchPreset preset = useCase.createPreset(
                organizationId,
                request.userId(),
                request.name(),
                request.targetEntity(),
                request.filterJson(),
                request.isShared()
        );
        return ResponseEntity.ok(toResponse(preset));
    }

    @PutMapping("/{presetId}")
    public ResponseEntity<PresetResponse> updatePreset(
            @PathVariable UUID presetId,
            @RequestBody UpdatePresetRequest request) {
        SavedSearchPreset preset = useCase.updatePreset(presetId, request.name(), request.filterJson(), request.isShared());
        return ResponseEntity.ok(toResponse(preset));
    }

    @PostMapping("/{presetId}/set-default")
    public ResponseEntity<PresetResponse> setDefaultPreset(
            @PathVariable UUID organizationId,
            @PathVariable UUID presetId,
            @RequestParam UUID userId) {
        SavedSearchPreset preset = useCase.setDefaultPreset(organizationId, userId, presetId);
        return ResponseEntity.ok(toResponse(preset));
    }

    @GetMapping
    public ResponseEntity<List<PresetResponse>> getPresets(
            @PathVariable UUID organizationId,
            @RequestParam UUID userId,
            @RequestParam String targetEntity) {
        List<PresetResponse> list = useCase.getAvailablePresets(organizationId, userId, targetEntity)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{presetId}")
    public ResponseEntity<Void> deletePreset(@PathVariable UUID presetId) {
        useCase.deletePreset(presetId);
        return ResponseEntity.noContent().build();
    }

    private PresetResponse toResponse(SavedSearchPreset p) {
        return new PresetResponse(
                p.getId(),
                p.getOrganizationId(),
                p.getUserId(),
                p.getName(),
                p.getTargetEntity(),
                p.getFilterJson(),
                p.isShared(),
                p.isDefault(),
                p.getCreatedAt()
        );
    }
}
