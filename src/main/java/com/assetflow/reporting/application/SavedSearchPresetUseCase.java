package com.assetflow.reporting.application;

import com.assetflow.reporting.domain.SavedSearchPreset;
import com.assetflow.reporting.domain.SavedSearchPresetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SavedSearchPresetUseCase {

    private final SavedSearchPresetRepository repository;

    public SavedSearchPresetUseCase(SavedSearchPresetRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SavedSearchPreset createPreset(
            UUID organizationId,
            UUID userId,
            String name,
            String targetEntity,
            String filterJson,
            boolean isShared) {
        SavedSearchPreset preset = SavedSearchPreset.create(
                organizationId,
                userId,
                name,
                targetEntity,
                filterJson,
                isShared
        );
        return repository.save(preset);
    }

    @Transactional
    public SavedSearchPreset updatePreset(UUID presetId, String name, String filterJson, boolean isShared) {
        SavedSearchPreset preset = repository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("Preset not found: " + presetId));
        preset.update(name, filterJson, isShared);
        return repository.save(preset);
    }

    @Transactional(readOnly = true)
    public List<SavedSearchPreset> getAvailablePresets(UUID organizationId, UUID userId, String targetEntity) {
        return repository.findAvailablePresets(organizationId, userId, targetEntity);
    }

    @Transactional
    public void deletePreset(UUID presetId) {
        repository.deleteById(presetId);
    }
}
