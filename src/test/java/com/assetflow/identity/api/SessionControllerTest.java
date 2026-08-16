package com.assetflow.identity.api;

import com.assetflow.identity.api.dto.DeviceSessionDto;
import com.assetflow.identity.application.GlobalLogoutUseCase;
import com.assetflow.identity.application.SessionManagementUseCase;
import com.assetflow.identity.infrastructure.security.JwtAuthenticationFilter;
import com.assetflow.identity.infrastructure.security.SecurityConfig;
import com.assetflow.shared.logging.CorrelationIdFilter;
import com.assetflow.shared.security.RateLimitingFilter;
import com.assetflow.shared.security.TenantContextFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SessionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, TenantContextFilter.class, RateLimitingFilter.class, CorrelationIdFilter.class})
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @MockitoBean
    private SessionManagementUseCase sessionManagementUseCase;

    @MockitoBean
    private GlobalLogoutUseCase globalLogoutUseCase;

    @Test
    @DisplayName("Should return 4xx client error when unauthenticated user accesses sessions")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/identity/sessions"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "EMPLOYEE")
    @DisplayName("Should return active device sessions for authenticated user")
    void shouldReturnActiveSessionsForUser() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID sessionId = UUID.randomUUID();
        DeviceSessionDto session = new DeviceSessionDto(sessionId, userId, Instant.now(), Instant.now().plusSeconds(3600), false);

        when(sessionManagementUseCase.listActiveSessions(userId)).thenReturn(List.of(session));

        mockMvc.perform(get("/api/v1/identity/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "EMPLOYEE")
    @DisplayName("Should revoke specific device session")
    void shouldRevokeSpecificSession() throws Exception {
        UUID sessionId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/identity/sessions/" + sessionId))
                .andExpect(status().isNoContent());

        verify(sessionManagementUseCase).revokeSession(sessionId, UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "EMPLOYEE")
    @DisplayName("Should execute global logout revoking all user sessions")
    void shouldExecuteGlobalLogout() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(post("/api/v1/identity/sessions/global-logout"))
                .andExpect(status().isNoContent());

        verify(globalLogoutUseCase).execute(userId);
    }
}
