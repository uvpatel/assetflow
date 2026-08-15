package com.assetflow.asset.api;

import com.assetflow.asset.application.ConsumableBatchUseCase;
import com.assetflow.asset.domain.ConsumableBatch;
import com.assetflow.shared.security.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consumable-batches")
public class ConsumableBatchController {

    private final ConsumableBatchUseCase useCase;

    public ConsumableBatchController(ConsumableBatchUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateConsumableBatchRequest(UUID assetId, String batchNumber, int quantity, Instant expiryDate) {}
    public record ConsumeBatchRequest(int quantity) {}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ConsumableBatch> createBatch(@RequestBody CreateConsumableBatchRequest request) {
        UUID orgId = TenantContext.requireOrganizationId();
        ConsumableBatch batch = useCase.createBatch(orgId, request.assetId(), request.batchNumber(), request.quantity(), request.expiryDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(batch);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<ConsumableBatch>> listBatches() {
        UUID orgId = TenantContext.requireOrganizationId();
        return ResponseEntity.ok(useCase.getBatchesForOrganization(orgId));
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<ConsumableBatch>> getBatchesForAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(useCase.getBatchesForAsset(assetId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ConsumableBatch> getBatch(@PathVariable UUID id) {
        return useCase.getBatch(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ConsumableBatch> consume(@PathVariable UUID id, @RequestBody ConsumeBatchRequest request) {
        return ResponseEntity.ok(useCase.consumeQuantity(id, request.quantity()));
    }
}
