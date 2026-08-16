package com.assetflow.financial.infrastructure.persistence;

import com.assetflow.financial.domain.DepreciationForecastProjection;
import com.assetflow.financial.domain.DepreciationForecastProjectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaDepreciationForecastProjectionRepositoryAdapter implements DepreciationForecastProjectionRepository {

    private final DepreciationForecastProjectionJpaRepository jpaRepository;

    public JpaDepreciationForecastProjectionRepositoryAdapter(DepreciationForecastProjectionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DepreciationForecastProjection save(DepreciationForecastProjection projection) {
        return jpaRepository.save(projection);
    }

    @Override
    public Optional<DepreciationForecastProjection> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear) {
        return jpaRepository.findByOrganizationIdAndFiscalYear(organizationId, fiscalYear);
    }

    @Override
    public List<DepreciationForecastProjection> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findByOrganizationId(organizationId);
    }
}
