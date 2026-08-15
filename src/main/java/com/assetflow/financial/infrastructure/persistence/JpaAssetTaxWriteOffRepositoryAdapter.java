package com.assetflow.financial.infrastructure.persistence;

import com.assetflow.financial.domain.AssetTaxWriteOff;
import com.assetflow.financial.domain.AssetTaxWriteOffRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAssetTaxWriteOffRepositoryAdapter implements AssetTaxWriteOffRepository {

    private final SpringDataAssetTaxWriteOffRepository repository;

    public JpaAssetTaxWriteOffRepositoryAdapter(SpringDataAssetTaxWriteOffRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetTaxWriteOff save(AssetTaxWriteOff writeOff) {
        AssetTaxWriteOffJpaEntity entity = toEntity(writeOff);
        AssetTaxWriteOffJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AssetTaxWriteOff> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AssetTaxWriteOff> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AssetTaxWriteOffJpaEntity toEntity(AssetTaxWriteOff domain) {
        AssetTaxWriteOffJpaEntity entity = new AssetTaxWriteOffJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setFiscalYear(domain.getFiscalYear());
        entity.setWriteoffAmount(domain.getWriteoffAmount());
        entity.setRemainingTaxBasis(domain.getRemainingTaxBasis());
        entity.setWriteoffReason(domain.getWriteoffReason());
        entity.setStatus(domain.getStatus().name());
        entity.setApprovedBy(domain.getApprovedBy());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private AssetTaxWriteOff toDomain(AssetTaxWriteOffJpaEntity entity) {
        AssetTaxWriteOff.WriteOffStatus status = AssetTaxWriteOff.WriteOffStatus.APPROVED;
        if (entity.getStatus() != null) {
            try {
                status = AssetTaxWriteOff.WriteOffStatus.valueOf(entity.getStatus().toUpperCase());
            } catch (Exception ignored) {
            }
        }

        return new AssetTaxWriteOff(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getFiscalYear(),
                entity.getWriteoffAmount(),
                entity.getRemainingTaxBasis(),
                entity.getWriteoffReason(),
                status,
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getCreatedAt()
        );
    }
}
