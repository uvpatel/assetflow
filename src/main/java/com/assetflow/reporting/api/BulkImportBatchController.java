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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> createBatch(@RequestBody CreateBatchRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();
        BulkImportBatch batch = bulkImportBatchUseCase.createBatch(organizationId, request.filename(), request.totalRecords());
        return ResponseEntity.status(HttpStatus.CREATED).body(batch);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<List<BulkImportBatch>> listBatches() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return ResponseEntity.ok(bulkImportBatchUseCase.listBatches(organizationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<BulkImportBatch> getBatch(@PathVariable UUID id) {
        return bulkImportBatchUseCase.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> startProcessing(@PathVariable UUID id) {
        return ResponseEntity.ok(bulkImportBatchUseCase.startProcessing(id));
    }

    @PostMapping("/{id}/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> recordSuccess(@PathVariable UUID id) {
        return ResponseEntity.ok(bulkImportBatchUseCase.recordSuccess(id));
    }

    @PostMapping("/{id}/failure")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BulkImportBatch> recordFailure(@PathVariable UUID id) {
        return ResponseEntity.ok(bulkImportBatchUseCase.recordFailure(id));
    }
}
