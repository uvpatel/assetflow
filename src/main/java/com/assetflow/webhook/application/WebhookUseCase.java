package com.assetflow.webhook.application;

import com.assetflow.shared.security.TenantContext;
import com.assetflow.webhook.domain.WebhookSubscription;
import com.assetflow.webhook.domain.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WebhookUseCase {

    private final WebhookSubscriptionRepository repository;

    public WebhookUseCase(WebhookSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WebhookSubscription createSubscription(String targetUrl, String secret, String eventTypes) {
        UUID organizationId = TenantContext.requireOrganizationId();
        WebhookSubscription subscription = new WebhookSubscription(organizationId, targetUrl, secret, eventTypes);
        return repository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<WebhookSubscription> listSubscriptions() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return repository.findByOrganizationId(organizationId);
    }
}
