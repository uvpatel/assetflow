package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPurchaseOrderRepositoryAdapter implements PurchaseOrderRepository {

    private final SpringDataPurchaseOrderRepository repository;

    public JpaPurchaseOrderRepositoryAdapter(SpringDataPurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        PurchaseOrderJpaEntity entity = toEntity(purchaseOrder);
        PurchaseOrderJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PurchaseOrder> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PurchaseOrder> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private PurchaseOrderJpaEntity toEntity(PurchaseOrder domain) {
        PurchaseOrderJpaEntity entity = new PurchaseOrderJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setVendorId(domain.getVendorId());
        entity.setPoNumber(domain.getPoNumber());
        entity.setStatus(domain.getStatus().name());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private PurchaseOrder toDomain(PurchaseOrderJpaEntity entity) {
        return new PurchaseOrder(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getVendorId(),
                entity.getPoNumber(),
                PurchaseOrder.Status.valueOf(entity.getStatus()),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
