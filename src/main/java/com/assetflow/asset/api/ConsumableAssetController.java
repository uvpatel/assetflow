package com.assetflow.asset.api;

import com.assetflow.asset.application.ConsumableAssetUseCase;
import com.assetflow.asset.domain.ConsumableAsset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/consumables")
public class ConsumableAssetController {

    private final ConsumableAssetUseCase useCase;

    public ConsumableAssetController(ConsumableAssetUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateConsumableRequest(
            String name,
            String sku,
            UUID categoryId,
            int initialQuantity,
            int minimumThreshold,
            String unitOfMeasure,
            BigDecimal unitCost
    ) {}

    public record ConsumableResponse(
            UUID id,
            UUID organizationId,
            String name,
            String sku,
            UUID categoryId,
            int quantityOnHand,
            int minimumThreshold,
            String unitOfMeasure,
            BigDecimal unitCost,
            boolean isLowStock
    ) {}

    @PostMapping
    public ResponseEntity<ConsumableResponse> createConsumable(
            @PathVariable UUID organizationId,
            @RequestBody CreateConsumableRequest request) {
        ConsumableAsset asset = useCase.createConsumable(
                organizationId,
                request.name(),
                request.sku(),
                request.categoryId(),
                request.initialQuantity(),
                request.minimumThreshold(),
                request.unitOfMeasure(),
                request.unitCost()
        );
        return ResponseEntity.ok(toResponse(asset));
    }

    @PostMapping("/{assetId}/restock")
    public ResponseEntity<ConsumableResponse> restock(
            @PathVariable UUID assetId,
            @RequestParam int amount) {
        ConsumableAsset asset = useCase.restock(assetId, amount);
        return ResponseEntity.ok(toResponse(asset));
    }

    @PostMapping("/{assetId}/disburse")
    public ResponseEntity<ConsumableResponse> disburse(
            @PathVariable UUID assetId,
            @RequestParam int amount) {
        ConsumableAsset asset = useCase.disburse(assetId, amount);
        return ResponseEntity.ok(toResponse(asset));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ConsumableResponse>> getLowStockAssets(@PathVariable UUID organizationId) {
        List<ConsumableResponse> list = useCase.getLowStockAssets(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private ConsumableResponse toResponse(ConsumableAsset c) {
        return new ConsumableResponse(
                c.getId(),
                c.getOrganizationId(),
                c.getName(),
                c.getSku(),
                c.getCategoryId(),
                c.getQuantityOnHand(),
                c.getMinimumThreshold(),
                c.getUnitOfMeasure(),
                c.getUnitCost(),
                c.isBelowThreshold()
        );
    }
}
