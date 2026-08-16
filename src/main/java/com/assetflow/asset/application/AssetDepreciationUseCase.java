package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRunRepository;
import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AssetDepreciationUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetDepreciationUseCase.class);

    private final AssetDepreciationScheduleRepository repository;
    private final AssetDepreciationPeriodClosingRunRepository periodClosingRunRepository;

    public AssetDepreciationUseCase(AssetDepreciationScheduleRepository repository,
                                    AssetDepreciationPeriodClosingRunRepository periodClosingRunRepository) {
        this.repository = repository;
        this.periodClosingRunRepository = periodClosingRunRepository;
    }

    @Transactional
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
        log.info("Created depreciation schedule for asset {} with method {}", assetId, method);
        return repository.save(schedule);
    }

    @Transactional
    public AssetDepreciationSchedule calculateDepreciation(UUID organizationId, UUID assetId, int elapsedMonths) {
        AssetDepreciationSchedule schedule = repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .orElseThrow(() -> new DomainViolationException("Schedule Not Found", "Depreciation schedule not found for asset " + assetId));
        schedule.calculate(elapsedMonths);
        return repository.save(schedule);
    }

    @Transactional
    public AssetDepreciationPeriodClosingRun runPeriodClosing(UUID organizationId, int year, int month, int elapsedMonths) {
        periodClosingRunRepository.findByOrganizationIdAndPeriodYearAndPeriodMonth(organizationId, year, month)
                .ifPresent(existing -> {
                    throw new DomainViolationException("Period Already Closed", "Depreciation period closing run already executed for year " + year + " month " + month);
                });

        List<AssetDepreciationSchedule> schedules = repository.findByOrganizationId(organizationId);
        BigDecimal totalDepreciation = BigDecimal.ZERO;
        int processedCount = 0;

        for (AssetDepreciationSchedule schedule : schedules) {
            BigDecimal previousAccumulated = schedule.getAccumulatedDepreciation();
            schedule.calculate(elapsedMonths);
            BigDecimal currentAccumulated = schedule.getAccumulatedDepreciation();
            BigDecimal periodDepreciation = currentAccumulated.subtract(previousAccumulated).max(BigDecimal.ZERO);

            totalDepreciation = totalDepreciation.add(periodDepreciation);
            repository.save(schedule);
            processedCount++;
        }

        AssetDepreciationPeriodClosingRun run = new AssetDepreciationPeriodClosingRun(
                organizationId,
                year,
                month,
                processedCount,
                totalDepreciation
        );

        log.info("Executed period closing run for org {} (Year: {}, Month: {}, Schedules: {}, Total Dep: {})",
                organizationId, year, month, processedCount, totalDepreciation);
        return periodClosingRunRepository.save(run);
    }

    @Transactional(readOnly = true)
    public List<AssetDepreciationPeriodClosingRun> getPeriodClosingRuns(UUID organizationId) {
        return periodClosingRunRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public AssetDepreciationSchedule getSchedule(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .orElseThrow(() -> new DomainViolationException("Schedule Not Found", "Depreciation schedule not found for asset " + assetId));
    }
}
