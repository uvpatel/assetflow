package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.NotificationQueueMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationQueueMessageJpaRepository extends JpaRepository<NotificationQueueMessage, UUID> {

    @Query("SELECT m FROM NotificationQueueMessage m WHERE m.organizationId = :organizationId AND m.status = 'PENDING'")
    List<NotificationQueueMessage> findPendingMessagesByOrganizationId(UUID organizationId);
}
