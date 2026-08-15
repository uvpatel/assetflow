package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.TechnicianWorkload;
import com.assetflow.maintenance.domain.TechnicianWorkloadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TechnicianWorkloadUseCase {

    private final TechnicianWorkloadRepository repository;

    public TechnicianWorkloadUseCase(TechnicianWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TechnicianWorkload registerTechnician(
            UUID organizationId,
            UUID technicianEmployeeId,
            int maxTaskCapacity,
            String specialization) {
        TechnicianWorkload workload = TechnicianWorkload.register(
                organizationId,
                technicianEmployeeId,
                maxTaskCapacity,
                specialization
        );
        return repository.save(workload);
    }

    @Transactional
    public TechnicianWorkload assignTaskToTechnician(UUID organizationId, UUID technicianEmployeeId) {
        TechnicianWorkload workload = repository.findByOrganizationIdAndTechnicianEmployeeId(organizationId, technicianEmployeeId)
                .orElseThrow(() -> new IllegalArgumentException("Technician workload not found for employee: " + technicianEmployeeId));
        workload.assignTask();
        return repository.save(workload);
    }

    @Transactional
    public TechnicianWorkload completeTaskForTechnician(UUID organizationId, UUID technicianEmployeeId) {
        TechnicianWorkload workload = repository.findByOrganizationIdAndTechnicianEmployeeId(organizationId, technicianEmployeeId)
                .orElseThrow(() -> new IllegalArgumentException("Technician workload not found for employee: " + technicianEmployeeId));
        workload.completeTask();
        return repository.save(workload);
    }

    @Transactional(readOnly = true)
    public List<TechnicianWorkload> getAvailableTechnicians(UUID organizationId) {
        return repository.findAvailableTechnicians(organizationId);
    }
}
