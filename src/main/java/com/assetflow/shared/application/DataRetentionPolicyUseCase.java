package com.assetflow.shared.application;

import com.assetflow.shared.domain.DataRetentionPolicy;
import com.assetflow.shared.domain.DataRetentionPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DataRetentionPolicyUseCase {

    private final DataRetentionPolicyRepository repository;

    public DataRetentionPolicyUseCase(DataRetentionPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DataRetentionPolicy configurePolicy(
            UUID organizationId,
            String targetModule,
            int retentionDays,
            String actionStr) {
        DataRetentionPolicy.RetentionAction action = DataRetentionPolicy.RetentionAction.valueOf(actionStr.toUpperCase());
        DataRetentionPolicy policy = repository.findByOrganizationIdAndTargetModule(organizationId, targetModule)
                .map(existing -> {
                    existing.update(retentionDays, action, true);
                    return existing;
                })
                .orElseGet(() -> DataRetentionPolicy.create(organizationId, targetModule, retentionDays, action));
        return repository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<DataRetentionPolicy> getOrganizationPolicies(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
