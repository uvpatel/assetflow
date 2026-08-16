package com.assetflow.asset.application;

import com.assetflow.asset.domain.ConsumableAsset;
import com.assetflow.asset.domain.ConsumableAssetRepository;
import com.assetflow.asset.domain.ConsumableBatch;
import com.assetflow.asset.domain.ConsumableBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ConsumableBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsumableBatchUseCase.class);

    private final ConsumableBatchRepository repository;
    private final ConsumableAssetRepository consumableAssetRepository;

    public ConsumableBatchUseCase(ConsumableBatchRepository repository,
                                 ConsumableAssetRepository consumableAssetRepository) {
        this.repository = repository;
        this.consumableAssetRepository = consumableAssetRepository;
    }

    public record ReorderAlertSummary(
            UUID assetId,
            String assetName,
            String sku,
            int currentStock,
            int minimumThreshold,
            int expiringBatchCount,
            int recommendedReorderQuantity
    ) {}

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

    @Transactional(readOnly = true)
    public List<ConsumableBatch> getExpiringBatches(UUID organizationId, int daysThreshold) {
        Instant now = Instant.now();
        return repository.findByOrganizationId(organizationId)
                .stream()
                .filter(b -> b.getCurrentQuantity() > 0 && b.isExpiringWithin(now, daysThreshold))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertSummary> generateStockReorderAlerts(UUID organizationId) {
        Instant now = Instant.now();
        List<ConsumableAsset> assets = consumableAssetRepository.findByOrganizationId(organizationId);
        List<ConsumableBatch> batches = repository.findByOrganizationId(organizationId);
        List<ReorderAlertSummary> alerts = new ArrayList<>();

        for (ConsumableAsset asset : assets) {
            long expiringBatchCount = batches.stream()
                    .filter(b -> b.getAssetId().equals(asset.getId()) && b.getCurrentQuantity() > 0 && b.isExpiringWithin(now, 30))
                    .count();

            if (asset.isBelowThreshold() || expiringBatchCount > 0) {
                int recommendedQuantity = Math.max(0, (asset.getMinimumThreshold() * 2) - asset.getQuantityOnHand());
                if (recommendedQuantity == 0 && expiringBatchCount > 0) {
                    recommendedQuantity = asset.getMinimumThreshold();
                }

                alerts.add(new ReorderAlertSummary(
                        asset.getId(),
                        asset.getName(),
                        asset.getSku(),
                        asset.getQuantityOnHand(),
                        asset.getMinimumThreshold(),
                        (int) expiringBatchCount,
                        recommendedQuantity
                ));
            }
        }

        return alerts;
    }

    public ConsumableBatch consumeQuantity(UUID batchId, int quantity) {
        ConsumableBatch batch = repository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Consumable batch not found: " + batchId));
        batch.consume(quantity);
        return repository.save(batch);
    }
}
