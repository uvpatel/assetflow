package com.assetflow.webhook.api;

import com.assetflow.webhook.application.WebhookUseCase;
import com.assetflow.webhook.domain.WebhookSubscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookUseCase webhookUseCase;

    public WebhookController(WebhookUseCase webhookUseCase) {
        this.webhookUseCase = webhookUseCase;
    }

    @PostMapping
    public ResponseEntity<WebhookSubscription> createSubscription(
            @RequestParam String targetUrl,
            @RequestParam String secret,
            @RequestParam(defaultValue = "ASSET_CREATED,ASSET_ALLOCATED,MAINTENANCE_REQUESTED") String eventTypes) {
        WebhookSubscription subscription = webhookUseCase.createSubscription(targetUrl, secret, eventTypes);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping
    public ResponseEntity<List<WebhookSubscription>> listSubscriptions() {
        return ResponseEntity.ok(webhookUseCase.listSubscriptions());
    }
}
