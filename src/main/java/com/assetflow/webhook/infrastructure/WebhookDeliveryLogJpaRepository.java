package com.assetflow.webhook.infrastructure;

import com.assetflow.webhook.domain.WebhookDeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookDeliveryLogJpaRepository extends JpaRepository<WebhookDeliveryLog, UUID> {
    List<WebhookDeliveryLog> findBySubscriptionId(UUID subscriptionId);
}
