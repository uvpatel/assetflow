package com.assetflow.webhook.application;

import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import com.assetflow.webhook.domain.WebhookDeliveryLog;
import com.assetflow.webhook.domain.WebhookDeliveryLogRepository;
import com.assetflow.webhook.domain.WebhookHmacSigner;
import com.assetflow.webhook.domain.WebhookSubscription;
import com.assetflow.webhook.domain.WebhookSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(WebhookUseCase.class);

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryLogRepository deliveryLogRepository;

    public WebhookUseCase(WebhookSubscriptionRepository subscriptionRepository,
                          WebhookDeliveryLogRepository deliveryLogRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.deliveryLogRepository = deliveryLogRepository;
    }

    @Transactional
    public WebhookSubscription createSubscription(String targetUrl, String secret, String eventTypes) {
        UUID organizationId = TenantContext.requireOrganizationId();
        String effectiveSecret = (secret != null && !secret.isBlank())
                ? secret
                : "whsec_" + UUID.randomUUID().toString().replace("-", "");

        WebhookSubscription subscription = new WebhookSubscription(organizationId, targetUrl, effectiveSecret, eventTypes);
        log.info("Created webhook subscription for URL {} in organization {}", targetUrl, organizationId);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public WebhookDeliveryLog dispatchEvent(UUID subscriptionId, String eventType, String payloadJson) {
        UUID organizationId = TenantContext.requireOrganizationId();
        WebhookSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new DomainViolationException("Webhook Subscription Not Found", "Target subscription was not found."));

        if (!subscription.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot dispatch webhook for subscription belonging to another organization.");
        }

        String signature = WebhookHmacSigner.calculateSignature(payloadJson, subscription.getSecret());
        int simulatedResponseCode = 200;

        WebhookDeliveryLog deliveryLog = new WebhookDeliveryLog(
                organizationId,
                subscriptionId,
                eventType,
                payloadJson,
                simulatedResponseCode,
                signature
        );

        log.info("Dispatched event {} to webhook {} with HMAC signature {}", eventType, subscription.getTargetUrl(), signature);
        return deliveryLogRepository.save(deliveryLog);
    }

    @Transactional
    public void deleteSubscription(UUID subscriptionId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        WebhookSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new DomainViolationException("Webhook Subscription Not Found", "Target subscription was not found."));

        if (!subscription.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot delete subscription belonging to another organization.");
        }

        subscriptionRepository.deleteById(subscriptionId);
        log.info("Deleted webhook subscription {} for organization {}", subscriptionId, organizationId);
    }

    @Transactional(readOnly = true)
    public List<WebhookSubscription> listSubscriptions() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return subscriptionRepository.findByOrganizationId(organizationId);
    }
}
