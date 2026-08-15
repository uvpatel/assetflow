package com.assetflow.notification.domain;

import java.util.List;

public interface OutboxEventRepository {
    OutboxEvent save(OutboxEvent event);
    List<OutboxEvent> findPendingEvents();
}
