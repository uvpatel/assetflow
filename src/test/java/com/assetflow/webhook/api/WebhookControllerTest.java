package com.assetflow.webhook.api;

import com.assetflow.shared.error.GlobalExceptionHandler;
import com.assetflow.webhook.application.WebhookUseCase;
import com.assetflow.webhook.domain.WebhookDeliveryLog;
import com.assetflow.webhook.domain.WebhookSubscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WebhookUseCase webhookUseCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create webhook subscription via POST")
    void shouldCreateSubscription() throws Exception {
        UUID orgId = UUID.randomUUID();
        WebhookSubscription subscription = new WebhookSubscription(
                orgId,
                "https://api.example.com/webhooks",
                "super-secret-key",
                "ASSET_CREATED,ASSET_ALLOCATED"
        );

        when(webhookUseCase.createSubscription(
                eq("https://api.example.com/webhooks"), eq("super-secret-key"), eq("ASSET_CREATED,ASSET_ALLOCATED")
        )).thenReturn(subscription);

        mockMvc.perform(post("/webhooks/subscriptions")
                        .param("targetUrl", "https://api.example.com/webhooks")
                        .param("secret", "super-secret-key")
                        .param("eventTypes", "ASSET_CREATED,ASSET_ALLOCATED"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetUrl").value("https://api.example.com/webhooks"))
                .andExpect(jsonPath("$.eventTypes").value("ASSET_CREATED,ASSET_ALLOCATED"));
    }

    @Test
    @DisplayName("Should list webhook subscriptions via GET")
    void shouldListSubscriptions() throws Exception {
        UUID orgId = UUID.randomUUID();
        WebhookSubscription subscription = new WebhookSubscription(
                orgId,
                "https://api.example.com/webhooks",
                "secret",
                "ASSET_CREATED"
        );

        when(webhookUseCase.listSubscriptions()).thenReturn(List.of(subscription));

        mockMvc.perform(get("/webhooks/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetUrl").value("https://api.example.com/webhooks"));
    }

    @Test
    @DisplayName("Should test dispatch event via POST")
    void shouldTestDispatch() throws Exception {
        UUID subId = UUID.randomUUID();
        WebhookDeliveryLog deliveryLog = new WebhookDeliveryLog(
                UUID.randomUUID(),
                subId,
                "ASSET_CREATED",
                "{\"assetTag\":\"AST-001\"}",
                200,
                "sha256=abc123signature"
        );

        when(webhookUseCase.dispatchEvent(eq(subId), eq("ASSET_CREATED"), eq("{\"assetTag\":\"AST-001\"}")))
                .thenReturn(deliveryLog);

        WebhookController.TestDispatchDto dto = new WebhookController.TestDispatchDto(
                "ASSET_CREATED",
                "{\"assetTag\":\"AST-001\"}"
        );

        mockMvc.perform(post("/webhooks/subscriptions/{subscriptionId}/test", subId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType").value("ASSET_CREATED"))
                .andExpect(jsonPath("$.responseStatus").value(200))
                .andExpect(jsonPath("$.signature").value("sha256=abc123signature"));
    }

    @Test
    @DisplayName("Should delete webhook subscription via DELETE")
    void shouldDeleteSubscription() throws Exception {
        UUID subId = UUID.randomUUID();

        mockMvc.perform(delete("/webhooks/subscriptions/{subscriptionId}", subId))
                .andExpect(status().isNoContent());

        verify(webhookUseCase).deleteSubscription(subId);
    }
}
