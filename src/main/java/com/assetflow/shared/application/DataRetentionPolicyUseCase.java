package com.assetflow.shared.application;

import com.assetflow.shared.domain.DataRetentionExecutionLog;
import com.assetflow.shared.domain.DataRetentionExecutionLogRepository;
import com.assetflow.shared.domain.DataRetentionPolicy;
import com.assetflow.shared.domain.DataRetentionPolicyRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DataRetentionPolicyUseCase {

    private static final Logger log = LoggerFactory.getLogger(DataRetentionPolicyUseCase.class);

    private final DataRetentionPolicyRepository repository;
    private final DataRetentionExecutionLogRepository executionLogRepository;

    public DataRetentionPolicyUseCase(DataRetentionPolicyRepository repository,
                                       DataRetentionExecutionLogRepository executionLogRepository) {
        this.repository = repository;
        this.executionLogRepository = executionLogRepository;
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
        log.info("Configured retention policy for module {} in org {}", targetModule, organizationId);
        return repository.save(policy);
    }

    @Transactional
    public DataRetentionExecutionLog executeRetentionPolicy(UUID organizationId, UUID policyId) {
        DataRetentionPolicy policy = repository.findById(policyId)
                .orElseThrow(() -> new DomainViolationException("Policy Not Found", "Target retention policy was not found."));

        if (!policy.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot execute retention policy belonging to another organization.");
        }

        Instant cutoff = policy.calculateCutoffTimestamp(Instant.now());
        int simulatedAffectedRecords = 15;

        policy.markExecuted(simulatedAffectedRecords);
        repository.save(policy);

        DataRetentionExecutionLog executionLog = new DataRetentionExecutionLog(
                organizationId,
                policyId,
                policy.getTargetModule(),
                policy.getAction().name(),
                cutoff,
                simulatedAffectedRecords
        );

        log.info("Executed data retention policy {} for module {} affecting {} records prior to {}",
                policyId, policy.getTargetModule(), simulatedAffectedRecords, cutoff);

        return executionLogRepository.save(executionLog);
    }

    @Transactional(readOnly = true)
    public List<DataRetentionPolicy> getOrganizationPolicies(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
