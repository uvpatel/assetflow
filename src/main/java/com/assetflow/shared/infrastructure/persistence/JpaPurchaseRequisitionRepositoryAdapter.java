package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.PurchaseRequisition;
import com.assetflow.shared.domain.PurchaseRequisitionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPurchaseRequisitionRepositoryAdapter implements PurchaseRequisitionRepository {

    private final SpringDataPurchaseRequisitionRepository repository;

    public JpaPurchaseRequisitionRepositoryAdapter(SpringDataPurchaseRequisitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PurchaseRequisition save(PurchaseRequisition domain) {
        PurchaseRequisitionJpaEntity entity = toEntity(domain);
        PurchaseRequisitionJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PurchaseRequisition> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PurchaseRequisition> findByOrganizationIdAndId(UUID organizationId, UUID id) {
        return repository.findByOrganizationIdAndId(organizationId, id).map(this::toDomain);
    }

    @Override
    public List<PurchaseRequisition> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PurchaseRequisition> findByOrganizationIdAndRequesterId(UUID organizationId, UUID requesterId) {
        return repository.findByOrganizationIdAndRequesterId(organizationId, requesterId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PurchaseRequisition> findByOrganizationIdAndStatus(UUID organizationId, PurchaseRequisition.RequisitionStatus status) {
        return repository.findByOrganizationIdAndStatus(organizationId, status.name()).stream().map(this::toDomain).toList();
    }

    private PurchaseRequisitionJpaEntity toEntity(PurchaseRequisition domain) {
        PurchaseRequisitionJpaEntity entity = new PurchaseRequisitionJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setRequisitionNumber(domain.getRequisitionNumber());
        entity.setRequesterId(domain.getRequesterId());
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setTitle(domain.getTitle());
        entity.setBusinessJustification(domain.getBusinessJustification());
        entity.setEstimatedTotalAmount(domain.getEstimatedTotalAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setUrgency(domain.getUrgency().name());
        entity.setStatus(domain.getStatus().name());
        entity.setApproverId(domain.getApproverId());
        entity.setApprovalNotes(domain.getApprovalNotes());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setConvertedPurchaseOrderId(domain.getConvertedPurchaseOrderId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private PurchaseRequisition toDomain(PurchaseRequisitionJpaEntity entity) {
        PurchaseRequisition.RequisitionStatus status = PurchaseRequisition.RequisitionStatus.PENDING_APPROVAL;
        try {
            status = PurchaseRequisition.RequisitionStatus.valueOf(entity.getStatus().toUpperCase());
        } catch (Exception ignored) {}

        PurchaseRequisition.Urgency urgency = PurchaseRequisition.Urgency.NORMAL;
        try {
            urgency = PurchaseRequisition.Urgency.valueOf(entity.getUrgency().toUpperCase());
        } catch (Exception ignored) {}

        return new PurchaseRequisition(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getRequisitionNumber(),
                entity.getRequesterId(),
                entity.getDepartmentId(),
                entity.getTitle(),
                entity.getBusinessJustification(),
                entity.getEstimatedTotalAmount(),
                entity.getCurrency(),
                urgency,
                status,
                entity.getApproverId(),
                entity.getApprovalNotes(),
                entity.getApprovedAt(),
                entity.getConvertedPurchaseOrderId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
