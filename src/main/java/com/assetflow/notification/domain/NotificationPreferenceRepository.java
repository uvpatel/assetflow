package com.assetflow.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository {
    NotificationPreference save(NotificationPreference preference);
    Optional<NotificationPreference> findById(UUID id);
    Optional<NotificationPreference> findByOrganizationIdAndUserIdAndNotificationType(UUID organizationId, UUID userId, String notificationType);
    List<NotificationPreference> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}
