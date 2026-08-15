package com.assetflow.asset.application;

import com.assetflow.asset.domain.ConsumableAsset;
import com.assetflow.asset.domain.ConsumableAssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ConsumableAssetUseCase {

    private final ConsumableAssetRepository repository;

    public ConsumableAssetUseCase(ConsumableAssetRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ConsumableAsset createConsumable(
            UUID organizationId,
            String name,
            String sku,
            UUID categoryId,
            int initialQuantity,
            int minimumThreshold,
            String unitOfMeasure,
            BigDecimal unitCost) {
        ConsumableAsset asset = ConsumableAsset.create(
                organizationId,
                name,
                sku,
                categoryId,
                initialQuantity,
                minimumThreshold,
                unitOfMeasure,
                unitCost
        );
        return repository.save(asset);
    }

    @Transactional
    public ConsumableAsset restock(UUID assetId, int amount) {
        ConsumableAsset asset = repository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Consumable asset not found: " + assetId));
        asset.restock(amount);
        return repository.save(asset);
    }

    @Transactional
    public ConsumableAsset disburse(UUID assetId, int amount) {
        ConsumableAsset asset = repository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Consumable asset not found: " + assetId));
        asset.disburse(amount);
        return repository.save(asset);
    }

    @Transactional(readOnly = true)
    public List<ConsumableAsset> getLowStockAssets(UUID organizationId) {
        return repository.findLowStockAssets(organizationId);
    }
}
