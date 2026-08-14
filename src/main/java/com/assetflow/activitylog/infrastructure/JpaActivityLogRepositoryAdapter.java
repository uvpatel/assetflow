package com.assetflow.activitylog.infrastructure;

import com.assetflow.activitylog.domain.ActivityLog;
import com.assetflow.activitylog.domain.ActivityLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

interface SpringDataActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByOrganizationId(UUID organizationId);
}

@Repository
public class JpaActivityLogRepositoryAdapter implements ActivityLogRepository {

    private final SpringDataActivityLogRepository repository;

    public JpaActivityLogRepositoryAdapter(SpringDataActivityLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActivityLog save(ActivityLog log) {
        return repository.save(log);
    }

    @Override
    public List<ActivityLog> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
