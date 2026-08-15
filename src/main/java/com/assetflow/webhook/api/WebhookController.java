package com.assetflow.webhook.api;

import com.assetflow.webhook.application.WebhookUseCase;
import com.assetflow.webhook.domain.WebhookDeliveryLog;
import com.assetflow.webhook.domain.WebhookSubscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/webhooks/subscriptions")
public class WebhookController {

    private final WebhookUseCase webhookUseCase;

    public WebhookController(WebhookUseCase webhookUseCase) {
        this.webhookUseCase = webhookUseCase;
    }

    public record TestDispatchDto(
            String eventType,
            String payloadJson
    ) {}

    @PostMapping
    public ResponseEntity<WebhookSubscription> createSubscription(
            @RequestParam String targetUrl,
            @RequestParam(required = false) String secret,
            @RequestParam(defaultValue = "ASSET_CREATED,ASSET_ALLOCATED,MAINTENANCE_REQUESTED") String eventTypes) {
        WebhookSubscription subscription = webhookUseCase.createSubscription(targetUrl, secret, eventTypes);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping
    public ResponseEntity<List<WebhookSubscription>> listSubscriptions() {
        return ResponseEntity.ok(webhookUseCase.listSubscriptions());
    }

    @PostMapping("/{subscriptionId}/test")
    public ResponseEntity<WebhookDeliveryLog> testDispatch(
            @PathVariable UUID subscriptionId,
            @RequestBody TestDispatchDto dto) {
        WebhookDeliveryLog log = webhookUseCase.dispatchEvent(subscriptionId, dto.eventType(), dto.payloadJson());
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable UUID subscriptionId) {
        webhookUseCase.deleteSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }
}
