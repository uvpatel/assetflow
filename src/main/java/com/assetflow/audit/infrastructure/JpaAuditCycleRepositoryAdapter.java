package com.assetflow.audit.infrastructure;

import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.audit.domain.AuditItem;
import com.assetflow.audit.domain.AuditItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAuditCycleRepository extends JpaRepository<AuditCycle, UUID> {
    List<AuditCycle> findByOrganizationId(UUID organizationId);
}

interface SpringDataAuditItemRepository extends JpaRepository<AuditItem, UUID> {
    List<AuditItem> findByAuditCycleId(UUID auditCycleId);
}

@Repository
public class JpaAuditCycleRepositoryAdapter implements AuditCycleRepository {

    private final SpringDataAuditCycleRepository repository;

    public JpaAuditCycleRepositoryAdapter(SpringDataAuditCycleRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditCycle save(AuditCycle auditCycle) {
        return repository.save(auditCycle);
    }

    @Override
    public Optional<AuditCycle> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AuditCycle> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}

@Repository
class JpaAuditItemRepositoryAdapter implements AuditItemRepository {

    private final SpringDataAuditItemRepository repository;

    public JpaAuditItemRepositoryAdapter(SpringDataAuditItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditItem save(AuditItem item) {
        return repository.save(item);
    }

    @Override
    public List<AuditItem> saveAll(List<AuditItem> items) {
        return repository.saveAll(items);
    }

    @Override
    public Optional<AuditItem> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AuditItem> findByAuditCycleId(UUID auditCycleId) {
        return repository.findByAuditCycleId(auditCycleId);
    }
}
