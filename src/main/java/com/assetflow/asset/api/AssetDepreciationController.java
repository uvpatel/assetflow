package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetDepreciationUseCase;
import com.assetflow.asset.domain.AssetDepreciationSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/assets/{assetId}/depreciation")
public class AssetDepreciationController {

    private final AssetDepreciationUseCase useCase;

    public AssetDepreciationController(AssetDepreciationUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateDepreciationRequest(
            String method,
            BigDecimal initialCost,
            BigDecimal salvageValue,
            int usefulLifeMonths
    ) {}

    public record DepreciationResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            String method,
            BigDecimal initialCost,
            BigDecimal salvageValue,
            int usefulLifeMonths,
            BigDecimal currentBookValue,
            BigDecimal accumulatedDepreciation
    ) {}

    @PostMapping
    public ResponseEntity<DepreciationResponse> createSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestBody CreateDepreciationRequest request) {
        AssetDepreciationSchedule schedule = useCase.createSchedule(
                organizationId,
                assetId,
                request.method(),
                request.initialCost(),
                request.salvageValue(),
                request.usefulLifeMonths()
        );
        return ResponseEntity.ok(toResponse(schedule));
    }

    @PostMapping("/calculate")
    public ResponseEntity<DepreciationResponse> calculate(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestParam int elapsedMonths) {
        AssetDepreciationSchedule schedule = useCase.calculateDepreciation(organizationId, assetId, elapsedMonths);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @GetMapping
    public ResponseEntity<DepreciationResponse> getSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        AssetDepreciationSchedule schedule = useCase.getSchedule(organizationId, assetId);
        return ResponseEntity.ok(toResponse(schedule));
    }

    private DepreciationResponse toResponse(AssetDepreciationSchedule schedule) {
        return new DepreciationResponse(
                schedule.getId(),
                schedule.getOrganizationId(),
                schedule.getAssetId(),
                schedule.getDepreciationMethod().name(),
                schedule.getInitialCost(),
                schedule.getSalvageValue(),
                schedule.getUsefulLifeMonths(),
                schedule.getCurrentBookValue(),
                schedule.getAccumulatedDepreciation()
        );
    }
}
