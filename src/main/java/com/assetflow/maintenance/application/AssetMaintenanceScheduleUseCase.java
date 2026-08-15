package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import com.assetflow.maintenance.domain.AssetMaintenanceScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssetMaintenanceScheduleUseCase {

    private final AssetMaintenanceScheduleRepository repository;

    public AssetMaintenanceScheduleUseCase(AssetMaintenanceScheduleRepository repository) {
        this.repository = repository;
    }

    public AssetMaintenanceSchedule createSchedule(
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate) {
        AssetMaintenanceSchedule schedule = AssetMaintenanceSchedule.create(
                organizationId,
                assetId,
                title,
                frequencyDays,
                firstDueDate
        );
        return repository.save(schedule);
    }

    public AssetMaintenanceSchedule markMaintenancePerformed(UUID scheduleId, Instant performedAt) {
        AssetMaintenanceSchedule schedule = repository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance schedule not found: " + scheduleId));
        schedule.markPerformed(performedAt);
        return repository.save(schedule);
    }

    public List<AssetMaintenanceSchedule> getSchedulesForAsset(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
