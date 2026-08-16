package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.AssetMaintenanceScheduleUseCase;
import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/maintenance-schedules")
public class AssetMaintenanceScheduleController {

    private final AssetMaintenanceScheduleUseCase useCase;

    public AssetMaintenanceScheduleController(AssetMaintenanceScheduleUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateScheduleRequest(
            UUID assetId,
            String title,
            int frequencyDays,
            Instant firstDueDate,
            boolean autoGenerateWorkOrder,
            String priority
    ) {}

    public record ScheduleResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String title,
            int frequencyDays,
            Instant nextDueDate,
            Instant lastPerformedAt,
            String status,
            boolean autoGenerateWorkOrder,
            String priority
    ) {}

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @PathVariable UUID organizationId,
            @RequestBody CreateScheduleRequest request) {
        AssetMaintenanceSchedule schedule = useCase.createSchedule(
                organizationId,
                request.assetId(),
                request.title(),
                request.frequencyDays(),
                request.firstDueDate(),
                request.autoGenerateWorkOrder(),
                request.priority()
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

    @PostMapping("/{scheduleId}/pause")
    public ResponseEntity<ScheduleResponse> pauseSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID scheduleId) {
        AssetMaintenanceSchedule schedule = useCase.pauseSchedule(organizationId, scheduleId);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @PostMapping("/{scheduleId}/resume")
    public ResponseEntity<ScheduleResponse> resumeSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID scheduleId) {
        AssetMaintenanceSchedule schedule = useCase.resumeSchedule(organizationId, scheduleId);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @PostMapping("/generate-due-work-orders")
    public ResponseEntity<List<MaintenanceRequest>> generateDueWorkOrders(
            @PathVariable UUID organizationId) {
        List<MaintenanceRequest> generated = useCase.generateDueWorkOrders(organizationId);
        return ResponseEntity.ok(generated);
    }

    @GetMapping("/asset/{assetId}")
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
                s.getStatus().name(),
                s.isAutoGenerateWorkOrder(),
                s.getPriority()
        );
    }
}
