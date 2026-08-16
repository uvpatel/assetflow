package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetDepreciationUseCase;
import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import com.assetflow.asset.domain.AssetDepreciationSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}")
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

    public record RunPeriodClosingRequest(
            int year,
            int month,
            int elapsedMonths
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

    @PostMapping("/assets/{assetId}/depreciation")
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

    @PostMapping("/assets/{assetId}/depreciation/calculate")
    public ResponseEntity<DepreciationResponse> calculate(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId,
            @RequestParam int elapsedMonths) {
        AssetDepreciationSchedule schedule = useCase.calculateDepreciation(organizationId, assetId, elapsedMonths);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @GetMapping("/assets/{assetId}/depreciation")
    public ResponseEntity<DepreciationResponse> getSchedule(
            @PathVariable UUID organizationId,
            @PathVariable UUID assetId) {
        AssetDepreciationSchedule schedule = useCase.getSchedule(organizationId, assetId);
        return ResponseEntity.ok(toResponse(schedule));
    }

    @PostMapping("/depreciation/period-close")
    public ResponseEntity<AssetDepreciationPeriodClosingRun> runPeriodClosing(
            @PathVariable UUID organizationId,
            @RequestBody RunPeriodClosingRequest request) {
        AssetDepreciationPeriodClosingRun run = useCase.runPeriodClosing(
                organizationId,
                request.year(),
                request.month(),
                request.elapsedMonths()
        );
        return ResponseEntity.ok(run);
    }

    @GetMapping("/depreciation/period-closing-runs")
    public ResponseEntity<List<AssetDepreciationPeriodClosingRun>> getPeriodClosingRuns(
            @PathVariable UUID organizationId) {
        List<AssetDepreciationPeriodClosingRun> runs = useCase.getPeriodClosingRuns(organizationId);
        return ResponseEntity.ok(runs);
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
