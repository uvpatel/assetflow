package com.assetflow.shared.application;

import com.assetflow.shared.domain.FeatureFlag;
import com.assetflow.shared.domain.FeatureFlagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FeatureFlagUseCase {

    private final FeatureFlagRepository repository;

    public FeatureFlagUseCase(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureFlag createFlag(UUID organizationId, String flagKey, String description, boolean isEnabled) {
        FeatureFlag flag = FeatureFlag.create(organizationId, flagKey, description, isEnabled);
        return repository.save(flag);
    }

    @Transactional
    public FeatureFlag toggleFlag(UUID organizationId, String flagKey, boolean enabled) {
        FeatureFlag flag = repository.findByOrganizationIdAndFlagKey(organizationId, flagKey)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found for key: " + flagKey));
        flag.toggle(enabled);
        return repository.save(flag);
    }

    @Transactional(readOnly = true)
    public boolean isFeatureEnabled(UUID organizationId, String flagKey, boolean defaultValue) {
        return repository.findByOrganizationIdAndFlagKey(organizationId, flagKey)
                .map(FeatureFlag::isEnabled)
                .orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> getFlagsForOrganization(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
