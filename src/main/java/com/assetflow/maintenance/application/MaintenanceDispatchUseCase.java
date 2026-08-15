package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicy;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicyRepository;
import com.assetflow.maintenance.domain.TechnicianWorkload;
import com.assetflow.maintenance.domain.TechnicianWorkloadRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceDispatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceDispatchUseCase.class);

    private final MaintenanceRequestRepository requestRepository;
    private final TechnicianWorkloadRepository workloadRepository;
    private final MaintenanceSlaPolicyRepository slaPolicyRepository;

    public MaintenanceDispatchUseCase(MaintenanceRequestRepository requestRepository,
                                      TechnicianWorkloadRepository workloadRepository,
                                      MaintenanceSlaPolicyRepository slaPolicyRepository) {
        this.requestRepository = requestRepository;
        this.workloadRepository = workloadRepository;
        this.slaPolicyRepository = slaPolicyRepository;
    }

    @Transactional
    public MaintenanceRequest dispatchWorkOrder(UUID requestId, UUID targetTechnicianId) {
        UUID organizationId = TenantContext.requireOrganizationId();

        MaintenanceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DomainViolationException("Maintenance Request Not Found", "Target maintenance request was not found."));

        if (!request.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot dispatch request belonging to another organization.");
        }

        UUID technicianId = targetTechnicianId;
        if (technicianId == null) {
            technicianId = autoBalanceTechnician(organizationId);
        }

        TechnicianWorkload workload = workloadRepository.findByOrganizationIdAndTechnicianEmployeeId(organizationId, technicianId)
                .orElseThrow(() -> new DomainViolationException("Technician Workload Not Found", "Technician workload record not found."));

        workload.assignTask();
        workloadRepository.save(workload);

        int slaHours = getSlaHoursForPriority(organizationId, request.getPriority());
        Instant targetResolutionAt = Instant.now().plus(slaHours, ChronoUnit.HOURS);

        request.approve(technicianId);
        request.setTargetResolutionAt(targetResolutionAt);
        request.setSlaStatus("IN_SLA");

        log.info("Dispatched maintenance work order {} to technician {} with target resolution at {}", requestId, technicianId, targetResolutionAt);
        return requestRepository.save(request);
    }

    @Transactional
    public int evaluateSlaStatuses() {
        UUID organizationId = TenantContext.requireOrganizationId();
        Instant now = Instant.now();

        List<MaintenanceRequest> openRequests = requestRepository.findByOrganizationId(organizationId).stream()
                .filter(req -> !"RESOLVED".equals(req.getStatus()))
                .toList();

        int breachedCount = 0;
        for (MaintenanceRequest req : openRequests) {
            String initialStatus = req.getSlaStatus();
            req.checkSlaBreach(now);
            if (!initialStatus.equals(req.getSlaStatus())) {
                requestRepository.save(req);
                breachedCount++;
            }
        }

        log.info("Evaluated SLA statuses for organization {}. Total breached: {}", organizationId, breachedCount);
        return breachedCount;
    }

    private UUID autoBalanceTechnician(UUID organizationId) {
        List<TechnicianWorkload> workloads = workloadRepository.findByOrganizationId(organizationId);
        TechnicianWorkload selected = workloads.stream()
                .filter(TechnicianWorkload::isAvailable)
                .min(Comparator.comparingInt(TechnicianWorkload::getActiveTaskCount))
                .orElseThrow(() -> new DomainViolationException("No Available Technician", "All technicians have reached maximum capacity."));

        return selected.getTechnicianEmployeeId();
    }

    private int getSlaHoursForPriority(UUID organizationId, String priorityStr) {
        try {
            MaintenanceSlaPolicy.Priority priority = MaintenanceSlaPolicy.Priority.valueOf(priorityStr.toUpperCase());
            return slaPolicyRepository.findByOrganizationIdAndPriority(organizationId, priority)
                    .map(MaintenanceSlaPolicy::getResolutionDeadlineHours)
                    .orElseGet(() -> defaultHours(priority));
        } catch (Exception e) {
            return 24;
        }
    }

    private int defaultHours(MaintenanceSlaPolicy.Priority priority) {
        return switch (priority) {
            case CRITICAL -> 4;
            case HIGH -> 12;
            case MEDIUM -> 24;
            case LOW -> 72;
        };
    }
}
