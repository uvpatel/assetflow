package com.assetflow.reporting.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedSearchPresetRepository {
    SavedSearchPreset save(SavedSearchPreset preset);
    Optional<SavedSearchPreset> findById(UUID id);
    List<SavedSearchPreset> findAvailablePresets(UUID organizationId, UUID userId, String targetEntity);
    void deleteById(UUID id);
}
