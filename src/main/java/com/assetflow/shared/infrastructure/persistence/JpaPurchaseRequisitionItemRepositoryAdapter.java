package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.PurchaseRequisitionItem;
import com.assetflow.shared.domain.PurchaseRequisitionItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPurchaseRequisitionItemRepositoryAdapter implements PurchaseRequisitionItemRepository {

    private final SpringDataPurchaseRequisitionItemRepository repository;

    public JpaPurchaseRequisitionItemRepositoryAdapter(SpringDataPurchaseRequisitionItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public PurchaseRequisitionItem save(PurchaseRequisitionItem item) {
        return repository.save(item);
    }

    @Override
    public Optional<PurchaseRequisitionItem> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<PurchaseRequisitionItem> findByRequisitionId(UUID requisitionId) {
        return repository.findByRequisitionId(requisitionId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
