package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetBundleUseCase;
import com.assetflow.asset.domain.AssetBundleItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/assets/{parentAssetId}/bundle-items")
public class AssetBundleController {

    private final AssetBundleUseCase useCase;

    public AssetBundleController(AssetBundleUseCase useCase) {
        this.useCase = useCase;
    }

    public record AddBundleItemRequest(
            UUID childAssetId,
            String relationshipType,
            int quantity,
            String notes
    ) {}

    public record BundleItemResponse(
            UUID id,
            UUID organizationId,
            UUID parentAssetId,
            UUID childAssetId,
            String relationshipType,
            int quantity,
            String notes,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<BundleItemResponse> addBundleItem(
            @PathVariable UUID organizationId,
            @PathVariable UUID parentAssetId,
            @RequestBody AddBundleItemRequest request) {
        AssetBundleItem item = useCase.addBundleItem(
                organizationId,
                parentAssetId,
                request.childAssetId(),
                request.relationshipType(),
                request.quantity(),
                request.notes()
        );
        return ResponseEntity.ok(toResponse(item));
    }

    @GetMapping
    public ResponseEntity<List<BundleItemResponse>> getBundleItems(
            @PathVariable UUID organizationId,
            @PathVariable UUID parentAssetId) {
        List<BundleItemResponse> list = useCase.getBundleItems(organizationId, parentAssetId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeBundleItem(@PathVariable UUID itemId) {
        useCase.removeBundleItem(itemId);
        return ResponseEntity.noContent().build();
    }

    private BundleItemResponse toResponse(AssetBundleItem item) {
        return new BundleItemResponse(
                item.getId(),
                item.getOrganizationId(),
                item.getParentAssetId(),
                item.getChildAssetId(),
                item.getRelationshipType().name(),
                item.getQuantity(),
                item.getNotes(),
                item.getCreatedAt()
        );
    }
}
