package com.assetflow.activitylog.application;

import com.assetflow.activitylog.domain.ActivityLog;
import com.assetflow.activitylog.domain.ActivityLogRepository;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityLogUseCase {

    private final ActivityLogRepository repository;

    public ActivityLogUseCase(ActivityLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void log(UUID actorUserId, String action, String entityType, UUID entityId, String details, String ipAddress) {
        UUID organizationId = TenantContext.requireOrganizationId();
        ActivityLog log = new ActivityLog(organizationId, actorUserId, action, entityType, entityId, details, ipAddress);
        repository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> listLogs() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return repository.findByOrganizationId(organizationId);
    }
}
