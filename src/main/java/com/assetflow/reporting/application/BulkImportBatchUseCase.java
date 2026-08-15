package com.assetflow.reporting.application;

import com.assetflow.reporting.domain.BulkImportBatch;
import com.assetflow.reporting.domain.BulkImportBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BulkImportBatchUseCase {

    private final BulkImportBatchRepository batchRepository;

    public BulkImportBatchUseCase(BulkImportBatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public BulkImportBatch createBatch(UUID tenantId, UUID initiatedBy, String filename, int totalRecords) {
        BulkImportBatch batch = new BulkImportBatch(tenantId, initiatedBy, filename, totalRecords);
        return batchRepository.save(batch);
    }

    @Transactional(readOnly = true)
    public Optional<BulkImportBatch> getBatchById(UUID tenantId, UUID batchId) {
        return batchRepository.findByIdAndTenantId(batchId, tenantId);
    }

    @Transactional(readOnly = true)
    public List<BulkImportBatch> listBatches(UUID tenantId) {
        return batchRepository.findAllByTenantId(tenantId);
    }

    public BulkImportBatch updateProgress(UUID tenantId, UUID batchId, int processed, int failed) {
        BulkImportBatch batch = batchRepository.findByIdAndTenantId(batchId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.updateProgress(processed, failed);
        return batchRepository.save(batch);
    }

    public BulkImportBatch markCompleted(UUID tenantId, UUID batchId) {
        BulkImportBatch batch = batchRepository.findByIdAndTenantId(batchId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.markCompleted();
        return batchRepository.save(batch);
    }

    public BulkImportBatch markFailed(UUID tenantId, UUID batchId, String errorMessage) {
        BulkImportBatch batch = batchRepository.findByIdAndTenantId(batchId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.markFailed(errorMessage);
        return batchRepository.save(batch);
    }
}
