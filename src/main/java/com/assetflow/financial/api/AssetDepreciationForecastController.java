package com.assetflow.financial.api;

import com.assetflow.financial.application.AssetDepreciationForecastUseCase;
import com.assetflow.financial.domain.DepreciationForecastProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/depreciation-forecasts")
public class AssetDepreciationForecastController {

    private final AssetDepreciationForecastUseCase useCase;

    public AssetDepreciationForecastController(AssetDepreciationForecastUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<DepreciationForecastProjection> generateForecast(
            @PathVariable UUID organizationId,
            @RequestParam int fiscalYear) {
        DepreciationForecastProjection forecast = useCase.generateForecast(organizationId, fiscalYear);
        return ResponseEntity.ok(forecast);
    }

    @GetMapping
    public ResponseEntity<List<DepreciationForecastProjection>> getForecasts(@PathVariable UUID organizationId) {
        List<DepreciationForecastProjection> list = useCase.getForecasts(organizationId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{fiscalYear}")
    public ResponseEntity<DepreciationForecastProjection> getForecastForFiscalYear(
            @PathVariable UUID organizationId,
            @PathVariable int fiscalYear) {
        return useCase.getForecastForFiscalYear(organizationId, fiscalYear)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
