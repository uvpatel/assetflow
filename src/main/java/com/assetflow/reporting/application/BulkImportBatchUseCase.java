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

    public BulkImportBatch createBatch(UUID organizationId, String filename, int totalRecords) {
        BulkImportBatch batch = BulkImportBatch.create(organizationId, filename, totalRecords);
        return batchRepository.save(batch);
    }

    @Transactional(readOnly = true)
    public Optional<BulkImportBatch> getBatchById(UUID batchId) {
        return batchRepository.findById(batchId);
    }

    @Transactional(readOnly = true)
    public List<BulkImportBatch> listBatches(UUID organizationId) {
        return batchRepository.findByOrganizationId(organizationId);
    }

    public BulkImportBatch startProcessing(UUID batchId) {
        BulkImportBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.startProcessing();
        return batchRepository.save(batch);
    }

    public BulkImportBatch recordSuccess(UUID batchId) {
        BulkImportBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.recordSuccess();
        return batchRepository.save(batch);
    }

    public BulkImportBatch recordFailure(UUID batchId) {
        BulkImportBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found: " + batchId));
        batch.recordFailure();
        return batchRepository.save(batch);
    }
}
