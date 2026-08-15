package com.assetflow.reporting.application;

import com.assetflow.reporting.domain.SavedSearchPreset;
import com.assetflow.reporting.domain.SavedSearchPresetRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SavedSearchPresetUseCase {

    private static final Logger log = LoggerFactory.getLogger(SavedSearchPresetUseCase.class);

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
                .orElseThrow(() -> new DomainViolationException("Preset Not Found", "Target saved search preset was not found."));
        preset.update(name, filterJson, isShared);
        return repository.save(preset);
    }

    @Transactional
    public SavedSearchPreset setDefaultPreset(UUID organizationId, UUID userId, UUID presetId) {
        SavedSearchPreset targetPreset = repository.findById(presetId)
                .orElseThrow(() -> new DomainViolationException("Preset Not Found", "Target saved search preset was not found."));

        if (!targetPreset.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot modify preset belonging to another organization.");
        }

        List<SavedSearchPreset> existingPresets = repository.findAvailablePresets(organizationId, userId, targetPreset.getTargetEntity());
        for (SavedSearchPreset p : existingPresets) {
            if (p.isDefault()) {
                p.unmarkDefault();
                repository.save(p);
            }
        }

        targetPreset.markDefault();
        log.info("Set search preset {} as default for user {} in organization {}", presetId, userId, organizationId);
        return repository.save(targetPreset);
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
