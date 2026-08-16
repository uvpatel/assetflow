package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRunRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetDepreciationPeriodClosingRunRepositoryAdapter implements AssetDepreciationPeriodClosingRunRepository {

    private final AssetDepreciationPeriodClosingRunJpaRepository jpaRepository;

    public JpaAssetDepreciationPeriodClosingRunRepositoryAdapter(AssetDepreciationPeriodClosingRunJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AssetDepreciationPeriodClosingRun save(AssetDepreciationPeriodClosingRun run) {
        return jpaRepository.save(run);
    }

    @Override
    public Optional<AssetDepreciationPeriodClosingRun> findByOrganizationIdAndPeriodYearAndPeriodMonth(UUID organizationId, int year, int month) {
        return jpaRepository.findByOrganizationIdAndPeriodYearAndPeriodMonth(organizationId, year, month);
    }

    @Override
    public List<AssetDepreciationPeriodClosingRun> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findByOrganizationId(organizationId);
    }
}
