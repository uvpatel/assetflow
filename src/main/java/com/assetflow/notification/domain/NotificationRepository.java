package com.assetflow.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    List<Notification> findUnreadByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    void deleteById(UUID id);
}
