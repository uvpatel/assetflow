package com.assetflow.reporting.infrastructure.persistence;

import com.assetflow.reporting.domain.BulkImportBatch;
import com.assetflow.reporting.domain.BulkImportBatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaBulkImportBatchRepositoryAdapter implements BulkImportBatchRepository {

    private final SpringDataBulkImportBatchRepository repository;

    public JpaBulkImportBatchRepositoryAdapter(SpringDataBulkImportBatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public BulkImportBatch save(BulkImportBatch batch) {
        BulkImportBatchJpaEntity entity = toEntity(batch);
        BulkImportBatchJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<BulkImportBatch> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BulkImportBatch> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private BulkImportBatchJpaEntity toEntity(BulkImportBatch domain) {
        BulkImportBatchJpaEntity entity = new BulkImportBatchJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setFilename(domain.getFilename());
        entity.setTotalRecords(domain.getTotalRecords());
        entity.setProcessedRecords(domain.getProcessedRecords());
        entity.setFailedRecords(domain.getFailedRecords());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private BulkImportBatch toDomain(BulkImportBatchJpaEntity entity) {
        return new BulkImportBatch(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getFilename(),
                entity.getTotalRecords(),
                entity.getProcessedRecords(),
                entity.getFailedRecords(),
                BulkImportBatch.Status.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
