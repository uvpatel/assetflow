package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationPreferenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataNotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    Optional<NotificationPreference> findByOrganizationIdAndUserIdAndNotificationType(UUID organizationId, UUID userId, String notificationType);
    List<NotificationPreference> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}

@Repository
public class JpaNotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {

    private final SpringDataNotificationPreferenceRepository repository;

    public JpaNotificationPreferenceRepositoryAdapter(SpringDataNotificationPreferenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public NotificationPreference save(NotificationPreference preference) {
        return repository.save(preference);
    }

    @Override
    public Optional<NotificationPreference> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<NotificationPreference> findByOrganizationIdAndUserIdAndNotificationType(UUID organizationId, UUID userId, String notificationType) {
        return repository.findByOrganizationIdAndUserIdAndNotificationType(organizationId, userId, notificationType);
    }

    @Override
    public List<NotificationPreference> findByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return repository.findByOrganizationIdAndUserId(organizationId, userId);
    }
}
