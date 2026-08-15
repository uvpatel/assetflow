package com.assetflow.webhook.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookSubscriptionRepository {
    WebhookSubscription save(WebhookSubscription subscription);
    Optional<WebhookSubscription> findById(UUID id);
    List<WebhookSubscription> findByOrganizationId(UUID organizationId);
    void deleteById(UUID id);
}
