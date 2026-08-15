package com.assetflow.maintenance.api;

import com.assetflow.maintenance.application.MaintenancePartsUseCase;
import com.assetflow.maintenance.domain.MaintenancePartItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/maintenances/{maintenanceId}/parts")
public class MaintenancePartsController {

    private final MaintenancePartsUseCase useCase;

    public MaintenancePartsController(MaintenancePartsUseCase useCase) {
        this.useCase = useCase;
    }

    public record AddPartRequest(
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost
    ) {}

    public record PartItemResponse(
            UUID id,
            UUID organizationId,
            UUID maintenanceId,
            String partName,
            String partNumber,
            int quantityUsed,
            BigDecimal unitCost,
            BigDecimal totalCost,
            Instant createdAt
    ) {}

    public record PartsCostSummaryResponse(
            UUID maintenanceId,
            BigDecimal totalPartsCost,
            int totalLineItems
    ) {}

    @PostMapping
    public ResponseEntity<PartItemResponse> addPart(
            @PathVariable UUID organizationId,
            @PathVariable UUID maintenanceId,
            @RequestBody AddPartRequest request) {
        MaintenancePartItem item = useCase.addPartToMaintenance(
                organizationId,
                maintenanceId,
                request.partName(),
                request.partNumber(),
                request.quantityUsed(),
                request.unitCost()
        );
        return ResponseEntity.ok(toResponse(item));
    }

    @GetMapping
    public ResponseEntity<List<PartItemResponse>> getParts(
            @PathVariable UUID organizationId,
            @PathVariable UUID maintenanceId) {
        List<PartItemResponse> list = useCase.getPartsForMaintenance(organizationId, maintenanceId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/total-cost")
    public ResponseEntity<PartsCostSummaryResponse> getTotalPartsCost(
            @PathVariable UUID organizationId,
            @PathVariable UUID maintenanceId) {
        List<MaintenancePartItem> parts = useCase.getPartsForMaintenance(organizationId, maintenanceId);
        BigDecimal totalCost = useCase.calculateTotalPartsCost(organizationId, maintenanceId);
        return ResponseEntity.ok(new PartsCostSummaryResponse(maintenanceId, totalCost, parts.size()));
    }

    private PartItemResponse toResponse(MaintenancePartItem item) {
        return new PartItemResponse(
                item.getId(),
                item.getOrganizationId(),
                item.getMaintenanceId(),
                item.getPartName(),
                item.getPartNumber(),
                item.getQuantityUsed(),
                item.getUnitCost(),
                item.getTotalCost(),
                item.getCreatedAt()
        );
    }
}
