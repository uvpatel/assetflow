package com.assetflow.activitylog.domain;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository {
    ActivityLog save(ActivityLog log);
    List<ActivityLog> findByOrganizationId(UUID organizationId);
}
