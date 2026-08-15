package com.assetflow.allocation.infrastructure.persistence;

import com.assetflow.allocation.domain.TemporaryAssetCheckout;
import com.assetflow.allocation.domain.TemporaryAssetCheckoutRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTemporaryAssetCheckoutRepositoryAdapter implements TemporaryAssetCheckoutRepository {

    private final SpringDataTemporaryAssetCheckoutRepository repository;

    public JpaTemporaryAssetCheckoutRepositoryAdapter(SpringDataTemporaryAssetCheckoutRepository repository) {
        this.repository = repository;
    }

    @Override
    public TemporaryAssetCheckout save(TemporaryAssetCheckout checkout) {
        TemporaryAssetCheckoutJpaEntity entity = toEntity(checkout);
        TemporaryAssetCheckoutJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TemporaryAssetCheckout> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TemporaryAssetCheckout> findActiveCheckoutByAssetId(UUID organizationId, UUID assetId) {
        return repository.findActiveCheckoutByAssetId(organizationId, assetId).map(this::toDomain);
    }

    @Override
    public List<TemporaryAssetCheckout> findByOrganizationIdAndBorrowerEmployeeId(UUID organizationId, UUID borrowerEmployeeId) {
        return repository.findByOrganizationIdAndBorrowerEmployeeId(organizationId, borrowerEmployeeId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TemporaryAssetCheckoutJpaEntity toEntity(TemporaryAssetCheckout domain) {
        TemporaryAssetCheckoutJpaEntity entity = new TemporaryAssetCheckoutJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setBorrowerEmployeeId(domain.getBorrowerEmployeeId());
        entity.setCheckoutTime(domain.getCheckoutTime());
        entity.setExpectedReturnTime(domain.getExpectedReturnTime());
        entity.setActualReturnTime(domain.getActualReturnTime());
        entity.setCheckoutConditionNotes(domain.getCheckoutConditionNotes());
        entity.setReturnConditionNotes(domain.getReturnConditionNotes());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private TemporaryAssetCheckout toDomain(TemporaryAssetCheckoutJpaEntity entity) {
        return new TemporaryAssetCheckout(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getBorrowerEmployeeId(),
                entity.getCheckoutTime(),
                entity.getExpectedReturnTime(),
                entity.getActualReturnTime(),
                entity.getCheckoutConditionNotes(),
                entity.getReturnConditionNotes(),
                TemporaryAssetCheckout.CheckoutStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
