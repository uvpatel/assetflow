package com.assetflow.reporting.api;

import com.assetflow.reporting.application.BulkImportBatchUseCase;
import com.assetflow.reporting.domain.BulkImportBatch;
import com.assetflow.shared.security.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/import-batches")
public class BulkImportBatchController {

    private final BulkImportBatchUseCase bulkImportBatchUseCase;

    public BulkImportBatchController(BulkImportBatchUseCase bulkImportBatchUseCase) {
        this.bulkImportBatchUseCase = bulkImportBatchUseCase;
    }

    public record CreateBatchRequest(String filename, int totalRecords) {}
    public record UpdateProgressRequest(int processed, int failed) {}
    public record MarkFailedRequest(String errorMessage) {}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> createBatch(@RequestBody CreateBatchRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        BulkImportBatch batch = bulkImportBatchUseCase.createBatch(tenantId, userId, request.filename(), request.totalRecords());
        return ResponseEntity.status(HttpStatus.CREATED).body(batch);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<List<BulkImportBatch>> listBatches() {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(bulkImportBatchUseCase.listBatches(tenantId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<BulkImportBatch> getBatch(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        return bulkImportBatchUseCase.getBatchById(tenantId, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> updateProgress(@PathVariable UUID id, @RequestBody UpdateProgressRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(bulkImportBatchUseCase.updateProgress(tenantId, id, request.processed(), request.failed()));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> markCompleted(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(bulkImportBatchUseCase.markCompleted(tenantId, id));
    }

    @PostMapping("/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> markFailed(@PathVariable UUID id, @RequestBody MarkFailedRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(bulkImportBatchUseCase.markFailed(tenantId, id, request.errorMessage()));
    }
}
