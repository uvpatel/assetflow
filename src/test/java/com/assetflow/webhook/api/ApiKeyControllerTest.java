package com.assetflow.webhook.api;

import com.assetflow.shared.error.GlobalExceptionHandler;
import com.assetflow.webhook.application.ApiKeyUseCase;
import com.assetflow.webhook.domain.ApiKey;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiKeyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApiKeyUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create API key and return plaintext secret via POST")
    void shouldCreateApiKey() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID keyId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plusSeconds(86400);

        ApiKey apiKey = new ApiKey(
                keyId,
                orgId,
                "CI Service Key",
                "ak_live_",
                "hashed_secret_value",
                "asset:read,asset:create",
                expiresAt,
                false,
                Instant.now(),
                Instant.now()
        );

        ApiKeyUseCase.GeneratedApiKey generated = new ApiKeyUseCase.GeneratedApiKey(apiKey, "ak_live_secret12345");

        when(useCase.createApiKey(eq(orgId), eq("CI Service Key"), eq("asset:read,asset:create"), eq(expiresAt)))
                .thenReturn(generated);

        ApiKeyController.CreateApiKeyRequest request = new ApiKeyController.CreateApiKeyRequest(
                "CI Service Key",
                "asset:read,asset:create",
                expiresAt
        );

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/api-keys", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(keyId.toString()))
                .andExpect(jsonPath("$.name").value("CI Service Key"))
                .andExpect(jsonPath("$.keyPrefix").value("ak_live_"))
                .andExpect(jsonPath("$.plainTextSecret").value("ak_live_secret12345"))
                .andExpect(jsonPath("$.scopes").value("asset:read,asset:create"));
    }

    @Test
    @DisplayName("Should revoke API key via POST")
    void shouldRevokeApiKey() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID apiKeyId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/api-keys/{apiKeyId}/revoke", orgId, apiKeyId))
                .andExpect(status().isNoContent());

        verify(useCase).revokeApiKey(apiKeyId);
    }

    @Test
    @DisplayName("Should fetch API key summaries without leaking secrets via GET")
    void shouldGetApiKeys() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID keyId = UUID.randomUUID();

        ApiKey apiKey = new ApiKey(
                keyId,
                orgId,
                "Read-only Integration",
                "ak_live_",
                "hashed_secret",
                "asset:read",
                null,
                false,
                Instant.now(),
                Instant.now()
        );

        when(useCase.getApiKeys(eq(orgId))).thenReturn(List.of(apiKey));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/api-keys", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(keyId.toString()))
                .andExpect(jsonPath("$[0].name").value("Read-only Integration"))
                .andExpect(jsonPath("$[0].keyPrefix").value("ak_live_"))
                .andExpect(jsonPath("$[0].isRevoked").value(false))
                .andExpect(jsonPath("$[0].plainTextSecret").doesNotExist());
    }
}
