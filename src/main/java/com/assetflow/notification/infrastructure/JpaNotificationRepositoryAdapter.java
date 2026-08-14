package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataNotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    List<Notification> findByOrganizationIdAndUserIdAndReadFalse(UUID organizationId, UUID userId);
}

interface SpringDataNotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    Optional<NotificationPreference> findByOrganizationIdAndUserIdAndNotificationType(UUID organizationId, UUID userId, String notificationType);
    List<NotificationPreference> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository repository;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Notification> findByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return repository.findByOrganizationIdAndUserId(organizationId, userId);
    }

    @Override
    public List<Notification> findUnreadByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return repository.findByOrganizationIdAndUserIdAndReadFalse(organizationId, userId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}

@Repository
class JpaNotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {

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
