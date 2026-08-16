package com.assetflow.financial.infrastructure.persistence;

import com.assetflow.financial.domain.DepreciationForecastProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepreciationForecastProjectionJpaRepository extends JpaRepository<DepreciationForecastProjection, UUID> {
    Optional<DepreciationForecastProjection> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear);
    List<DepreciationForecastProjection> findByOrganizationId(UUID organizationId);
}
