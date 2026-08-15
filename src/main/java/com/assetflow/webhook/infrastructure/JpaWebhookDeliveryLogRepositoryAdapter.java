package com.assetflow.webhook.infrastructure;

import com.assetflow.webhook.domain.WebhookDeliveryLog;
import com.assetflow.webhook.domain.WebhookDeliveryLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaWebhookDeliveryLogRepositoryAdapter implements WebhookDeliveryLogRepository {

    private final WebhookDeliveryLogJpaRepository jpaRepository;

    public JpaWebhookDeliveryLogRepositoryAdapter(WebhookDeliveryLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WebhookDeliveryLog save(WebhookDeliveryLog log) {
        return jpaRepository.save(log);
    }

    @Override
    public List<WebhookDeliveryLog> findBySubscriptionId(UUID subscriptionId) {
        return jpaRepository.findBySubscriptionId(subscriptionId);
    }
}
