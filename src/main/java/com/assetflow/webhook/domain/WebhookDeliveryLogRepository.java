package com.assetflow.webhook.domain;

import java.util.List;
import java.util.UUID;

public interface WebhookDeliveryLogRepository {
    WebhookDeliveryLog save(WebhookDeliveryLog log);
    List<WebhookDeliveryLog> findBySubscriptionId(UUID subscriptionId);
}
