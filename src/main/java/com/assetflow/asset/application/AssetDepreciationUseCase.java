package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AssetDepreciationUseCase {

    private final AssetDepreciationScheduleRepository repository;

    public AssetDepreciationUseCase(AssetDepreciationScheduleRepository repository) {
        this.repository = repository;
    }

    public AssetDepreciationSchedule createSchedule(
            UUID organizationId,
            UUID assetId,
            String methodStr,
            BigDecimal initialCost,
            BigDecimal salvageValue,
            int usefulLifeMonths) {
        AssetDepreciationSchedule.DepreciationMethod method = AssetDepreciationSchedule.DepreciationMethod.valueOf(methodStr.toUpperCase());
        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                organizationId,
                assetId,
                method,
                initialCost,
                salvageValue,
                usefulLifeMonths
        );
        return repository.save(schedule);
    }

    public AssetDepreciationSchedule calculateDepreciation(UUID organizationId, UUID assetId, int elapsedMonths) {
        AssetDepreciationSchedule schedule = repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .orElseThrow(() -> new IllegalArgumentException("Depreciation schedule not found for asset " + assetId));
        schedule.calculate(elapsedMonths);
        return repository.save(schedule);
    }

    public AssetDepreciationSchedule getSchedule(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .orElseThrow(() -> new IllegalArgumentException("Depreciation schedule not found for asset " + assetId));
    }
}
