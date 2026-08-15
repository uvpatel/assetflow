package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.AssetMaintenanceScheduleUseCase;
import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/assets/{assetId}/maintenance-schedules")
public class AssetMaintenanceScheduleController {

    private final AssetMaintenanceScheduleUseCase useCase;

    public AssetMaintenanceScheduleController(AssetMaintenanceScheduleUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateScheduleRequest(
            String title,
            int frequencyDays,
            Instant firstDueDate
    ) {}

    public record ScheduleResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant nextDueDate,
            Instant lastPerformedAt,
            String status
    ) {}

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody CreateScheduleRequest request) {
        AssetMaintenanceSchedule schedule = useCase.createSchedule(
                organizationId,
                assetId,
                request.title(),
                request.frequencyDays(),
                request.firstDueDate()
        );
        return ResponseEntity.ok(toResponse(schedule));
    }

    @PostMapping("/{scheduleId}/perform")
    public ResponseEntity<ScheduleResponse> markPerformed(
            @PathVariable UUID scheduleId,
            @RequestParam(required = false) Instant performedAt) {
        Instant actualPerformedAt = (performedAt != null) ? performedAt : Instant.now();
        AssetMaintenanceSchedule schedule = useCase.markMaintenancePerformed(scheduleId, actualPerformedAt);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        List<ScheduleResponse> list = useCase.getSchedulesForAsset(organizationId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private ScheduleResponse toResponse(AssetMaintenanceSchedule s) {
        return new ScheduleResponse(
                s.getId(),
                s.getOrganizationId(),
                s.getAssetId(),
                s.getTitle(),
                s.getFrequencyDays(),
                s.getNextDueDate(),
                s.getLastPerformedAt(),
                s.getStatus().name()
        );
    }
}
