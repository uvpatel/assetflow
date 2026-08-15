package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetWarranty;
import com.assetflow.asset.domain.AssetWarrantyRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetWarrantyRepositoryAdapter implements AssetWarrantyRepository {

    private final SpringDataAssetWarrantyRepository repository;

    public JpaAssetWarrantyRepositoryAdapter(SpringDataAssetWarrantyRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetWarranty save(AssetWarranty warranty) {
        AssetWarrantyJpaEntity entity = toEntity(warranty);
        AssetWarrantyJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AssetWarranty> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AssetWarranty> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AssetWarranty> findExpiringWarranties(UUID organizationId, Instant thresholdDate) {
        return repository.findExpiringWarranties(organizationId, thresholdDate)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AssetWarrantyJpaEntity toEntity(AssetWarranty domain) {
        AssetWarrantyJpaEntity entity = new AssetWarrantyJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setProviderName(domain.getProviderName());
        entity.setPolicyNumber(domain.getPolicyNumber());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setCoverageDetails(domain.getCoverageDetails());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private AssetWarranty toDomain(AssetWarrantyJpaEntity entity) {
        return new AssetWarranty(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getProviderName(),
                entity.getPolicyNumber(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCoverageDetails(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
