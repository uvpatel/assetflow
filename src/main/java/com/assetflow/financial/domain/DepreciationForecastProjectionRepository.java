package com.assetflow.financial.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepreciationForecastProjectionRepository {
    DepreciationForecastProjection save(DepreciationForecastProjection projection);
    Optional<DepreciationForecastProjection> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear);
    List<DepreciationForecastProjection> findByOrganizationId(UUID organizationId);
}
