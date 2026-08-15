package com.assetflow.asset.application;

import com.assetflow.asset.domain.ConsumableBatch;
import com.assetflow.asset.domain.ConsumableBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ConsumableBatchUseCase {

    private final ConsumableBatchRepository repository;

    public ConsumableBatchUseCase(ConsumableBatchRepository repository) {
        this.repository = repository;
    }

    public ConsumableBatch createBatch(UUID organizationId, UUID assetId, String batchNumber, int quantity, Instant expiryDate) {
        ConsumableBatch batch = ConsumableBatch.create(organizationId, assetId, batchNumber, quantity, expiryDate);
        return repository.save(batch);
    }

    @Transactional(readOnly = true)
    public Optional<ConsumableBatch> getBatch(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ConsumableBatch> getBatchesForAsset(UUID assetId) {
        return repository.findByAssetId(assetId);
    }

    @Transactional(readOnly = true)
    public List<ConsumableBatch> getBatchesForOrganization(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    public ConsumableBatch consumeQuantity(UUID batchId, int quantity) {
        ConsumableBatch batch = repository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Consumable batch not found: " + batchId));
        batch.consume(quantity);
        return repository.save(batch);
    }
}
