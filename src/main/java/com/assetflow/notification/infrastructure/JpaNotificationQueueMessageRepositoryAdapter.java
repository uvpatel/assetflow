package com.assetflow.notification.infrastructure;

import com.assetflow.notification.domain.NotificationQueueMessage;
import com.assetflow.notification.domain.NotificationQueueMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaNotificationQueueMessageRepositoryAdapter implements NotificationQueueMessageRepository {

    private final NotificationQueueMessageJpaRepository jpaRepository;

    public JpaNotificationQueueMessageRepositoryAdapter(NotificationQueueMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NotificationQueueMessage save(NotificationQueueMessage message) {
        return jpaRepository.save(message);
    }

    @Override
    public List<NotificationQueueMessage> findPendingMessagesByOrganizationId(UUID organizationId) {
        return jpaRepository.findPendingMessagesByOrganizationId(organizationId);
    }
}
