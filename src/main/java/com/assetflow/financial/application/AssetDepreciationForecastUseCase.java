package com.assetflow.financial.application;

import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
import com.assetflow.financial.domain.DepreciationForecastProjection;
import com.assetflow.financial.domain.DepreciationForecastProjectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssetDepreciationForecastUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetDepreciationForecastUseCase.class);

    private final DepreciationForecastProjectionRepository repository;
    private final AssetDepreciationScheduleRepository scheduleRepository;

    public AssetDepreciationForecastUseCase(DepreciationForecastProjectionRepository repository,
                                             AssetDepreciationScheduleRepository scheduleRepository) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public DepreciationForecastProjection generateForecast(UUID organizationId, int fiscalYear) {
        List<AssetDepreciationSchedule> schedules = scheduleRepository.findByOrganizationId(organizationId);

        BigDecimal totalProjectedDepreciation = BigDecimal.ZERO;
        BigDecimal totalEndingBookValue = BigDecimal.ZERO;

        for (AssetDepreciationSchedule schedule : schedules) {
            BigDecimal startBookValue = schedule.getCurrentBookValue();
            BigDecimal salvage = schedule.getSalvageValue();
            BigDecimal depreciableBase = startBookValue.subtract(salvage).max(BigDecimal.ZERO);

            BigDecimal annualDepreciation;
            if (schedule.getUsefulLifeMonths() > 0) {
                annualDepreciation = depreciableBase.multiply(new BigDecimal("12"))
                        .divide(new BigDecimal(schedule.getUsefulLifeMonths()), 2, java.math.RoundingMode.HALF_UP)
                        .min(depreciableBase);
            } else {
                annualDepreciation = BigDecimal.ZERO;
            }

            BigDecimal endingBookValue = startBookValue.subtract(annualDepreciation).max(salvage);

            totalProjectedDepreciation = totalProjectedDepreciation.add(annualDepreciation);
            totalEndingBookValue = totalEndingBookValue.add(endingBookValue);
        }

        Optional<DepreciationForecastProjection> existing = repository.findByOrganizationIdAndFiscalYear(organizationId, fiscalYear);
        DepreciationForecastProjection projection;
        if (existing.isPresent()) {
            projection = new DepreciationForecastProjection(organizationId, fiscalYear, totalProjectedDepreciation, totalEndingBookValue);
        } else {
            projection = new DepreciationForecastProjection(organizationId, fiscalYear, totalProjectedDepreciation, totalEndingBookValue);
        }

        log.info("Generated financial depreciation forecast for org {} (FY: {}, Projected Dep: {}, Ending Book Val: {})",
                organizationId, fiscalYear, totalProjectedDepreciation, totalEndingBookValue);
        return repository.save(projection);
    }

    @Transactional(readOnly = true)
    public List<DepreciationForecastProjection> getForecasts(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<DepreciationForecastProjection> getForecastForFiscalYear(UUID organizationId, int fiscalYear) {
        return repository.findByOrganizationIdAndFiscalYear(organizationId, fiscalYear);
    }
}
