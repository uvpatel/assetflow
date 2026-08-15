package com.assetflow.notification.domain;

import java.util.List;
import java.util.UUID;

public interface NotificationQueueMessageRepository {
    NotificationQueueMessage save(NotificationQueueMessage message);
    List<NotificationQueueMessage> findPendingMessagesByOrganizationId(UUID organizationId);
}
