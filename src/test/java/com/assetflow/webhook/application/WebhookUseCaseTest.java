package com.assetflow.webhook.application;

import com.assetflow.shared.security.TenantContext;
import com.assetflow.webhook.domain.WebhookDeliveryLog;
import com.assetflow.webhook.domain.WebhookDeliveryLogRepository;
import com.assetflow.webhook.domain.WebhookSubscription;
import com.assetflow.webhook.domain.WebhookSubscriptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookUseCaseTest {

    @Mock
    private WebhookSubscriptionRepository subscriptionRepository;

    @Mock
    private WebhookDeliveryLogRepository deliveryLogRepository;

    private WebhookUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new WebhookUseCase(subscriptionRepository, deliveryLogRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should create subscription with auto-generated secret if secret is null")
    void shouldCreateSubscriptionWithAutoSecret() {
        when(subscriptionRepository.save(any(WebhookSubscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        WebhookSubscription subscription = useCase.createSubscription(
                "https://api.partner.com/webhooks",
                null,
                "ASSET_CREATED"
        );

        assertThat(subscription.getOrganizationId()).isEqualTo(orgId);
        assertThat(subscription.getTargetUrl()).isEqualTo("https://api.partner.com/webhooks");
        assertThat(subscription.getSecret()).startsWith("whsec_");
    }

    @Test
    @DisplayName("Should dispatch event and log delivery with HMAC signature")
    void shouldDispatchEventAndLogDelivery() {
        UUID subId = UUID.randomUUID();
        WebhookSubscription subscription = new WebhookSubscription(
                orgId,
                "https://api.partner.com/webhooks",
                "whsec_testsecret123",
                "ASSET_CREATED"
        );

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));
        when(deliveryLogRepository.save(any(WebhookDeliveryLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String payload = "{\"event\":\"ASSET_CREATED\",\"assetId\":\"abc\"}";
        WebhookDeliveryLog log = useCase.dispatchEvent(subId, "ASSET_CREATED", payload);

        assertThat(log.getOrganizationId()).isEqualTo(orgId);
        assertThat(log.getSubscriptionId()).isEqualTo(subId);
        assertThat(log.getEventType()).isEqualTo("ASSET_CREATED");
        assertThat(log.getSignature()).startsWith("sha256=");
        assertThat(log.getResponseStatus()).isEqualTo(200);
        verify(deliveryLogRepository).save(any(WebhookDeliveryLog.class));
    }
}
