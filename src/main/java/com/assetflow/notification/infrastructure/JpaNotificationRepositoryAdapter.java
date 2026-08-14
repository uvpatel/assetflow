package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataNotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByOrganizationIdAndUserIdOrderByCreatedAtDesc(UUID organizationId, UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.organizationId = :orgId AND n.userId = :userId AND n.read = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByOrganizationIdAndUserId(@Param("orgId") UUID organizationId, @Param("userId") UUID userId);
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
        return repository.findByOrganizationIdAndUserIdOrderByCreatedAtDesc(organizationId, userId);
    }

    @Override
    public List<Notification> findUnreadByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return repository.findUnreadByOrganizationIdAndUserId(organizationId, userId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
