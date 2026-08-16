package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import com.assetflow.maintenance.domain.AssetMaintenanceScheduleRepository;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssetMaintenanceScheduleUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetMaintenanceScheduleUseCase.class);

    private final AssetMaintenanceScheduleRepository repository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public AssetMaintenanceScheduleUseCase(AssetMaintenanceScheduleRepository repository,
                                            MaintenanceRequestRepository maintenanceRequestRepository) {
        this.repository = repository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    @Transactional
    public AssetMaintenanceSchedule createSchedule(
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate,
            boolean autoGenerateWorkOrder,
            String priority) {
        AssetMaintenanceSchedule schedule = AssetMaintenanceSchedule.create(
                organizationId,
                assetId,
                title,
                frequencyDays,
                firstDueDate,
                autoGenerateWorkOrder,
                priority
        );
        log.info("Created maintenance schedule {} for asset {}", title, assetId);
        return repository.save(schedule);
    }

    @Transactional
    public AssetMaintenanceSchedule createSchedule(
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate) {
        return createSchedule(organizationId, assetId, title, frequencyDays, firstDueDate, true, "MEDIUM");
    }

    @Transactional
    public AssetMaintenanceSchedule markMaintenancePerformed(UUID scheduleId, Instant performedAt) {
        AssetMaintenanceSchedule schedule = repository.findById(scheduleId)
                .orElseThrow(() -> new DomainViolationException("Schedule Not Found", "Maintenance schedule not found: " + scheduleId));
        schedule.markPerformed(performedAt);
        log.info("Marked maintenance performed for schedule {}", scheduleId);
        return repository.save(schedule);
    }

    @Transactional
    public AssetMaintenanceSchedule pauseSchedule(UUID organizationId, UUID scheduleId) {
        AssetMaintenanceSchedule schedule = repository.findById(scheduleId)
                .orElseThrow(() -> new DomainViolationException("Schedule Not Found", "Maintenance schedule not found: " + scheduleId));

        if (!schedule.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot pause schedule belonging to another organization.");
        }

        schedule.pause();
        log.info("Paused maintenance schedule {}", scheduleId);
        return repository.save(schedule);
    }

    @Transactional
    public AssetMaintenanceSchedule resumeSchedule(UUID organizationId, UUID scheduleId) {
        AssetMaintenanceSchedule schedule = repository.findById(scheduleId)
                .orElseThrow(() -> new DomainViolationException("Schedule Not Found", "Maintenance schedule not found: " + scheduleId));

        if (!schedule.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot resume schedule belonging to another organization.");
        }

        schedule.resume();
        log.info("Resumed maintenance schedule {}", scheduleId);
        return repository.save(schedule);
    }

    @Transactional
    public List<MaintenanceRequest> generateDueWorkOrders(UUID organizationId) {
        Instant now = Instant.now();
        List<AssetMaintenanceSchedule> schedules = repository.findByOrganizationId(organizationId);
        List<MaintenanceRequest> generatedRequests = new ArrayList<>();

        for (AssetMaintenanceSchedule sched : schedules) {
            sched.checkOverdue(now);

            if (sched.isAutoGenerateWorkOrder() &&
                (sched.getStatus() == AssetMaintenanceSchedule.ScheduleStatus.ACTIVE || sched.getStatus() == AssetMaintenanceSchedule.ScheduleStatus.OVERDUE) &&
                !now.isBefore(sched.getNextDueDate())) {

                MaintenanceRequest request = new MaintenanceRequest(
                        organizationId,
                        sched.getAssetId(),
                        organizationId, // Default system requester
                        sched.getTitle(),
                        "Auto-generated PM Work Order for schedule: " + sched.getTitle(),
                        sched.getPriority()
                );

                MaintenanceRequest savedReq = maintenanceRequestRepository.save(request);
                generatedRequests.add(savedReq);

                sched.markPerformed(now);
                repository.save(sched);

                log.info("Generated PM maintenance request {} for asset {} from schedule {}", savedReq.getId(), sched.getAssetId(), sched.getId());
            }
        }

        return generatedRequests;
    }

    @Transactional(readOnly = true)
    public List<AssetMaintenanceSchedule> getSchedulesForAsset(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
