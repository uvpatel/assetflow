package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenanceSlaPolicy;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceSlaUseCase {

    private final MaintenanceSlaPolicyRepository repository;

    public MaintenanceSlaUseCase(MaintenanceSlaPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MaintenanceSlaPolicy configureSlaPolicy(
            UUID organizationId,
            String priorityStr,
            int responseDeadlineHours,
            int resolutionDeadlineHours,
            String escalationEmail) {
        MaintenanceSlaPolicy.Priority priority = MaintenanceSlaPolicy.Priority.valueOf(priorityStr.toUpperCase());
        MaintenanceSlaPolicy policy = MaintenanceSlaPolicy.create(
                organizationId,
                priority,
                responseDeadlineHours,
                resolutionDeadlineHours,
                escalationEmail
        );
        return repository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceSlaPolicy> getSlaPolicies(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
