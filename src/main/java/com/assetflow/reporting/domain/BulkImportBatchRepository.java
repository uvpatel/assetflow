package com.assetflow.reporting.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BulkImportBatchRepository {
    BulkImportBatch save(BulkImportBatch batch);
    Optional<BulkImportBatch> findById(UUID id);
    List<BulkImportBatch> findByOrganizationId(UUID organizationId);
}
