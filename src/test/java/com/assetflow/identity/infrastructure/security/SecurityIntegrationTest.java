package com.assetflow.identity.infrastructure.security;

import com.assetflow.identity.api.AuthController;
import com.assetflow.identity.application.LoginUseCase;
import com.assetflow.identity.application.LogoutUseCase;
import com.assetflow.identity.application.RefreshTokenUseCase;
import com.assetflow.identity.application.SignupUseCase;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.reporting.api.BulkImportBatchController;
import com.assetflow.reporting.application.BulkImportBatchUseCase;
import com.assetflow.shared.security.TenantContext;
import com.assetflow.shared.security.TenantContextFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assetflow.shared.security.RateLimitingFilter;

@WebMvcTest(controllers = {AuthController.class, BulkImportBatchController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, TenantContextFilter.class, RateLimitingFilter.class})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProviderPort tokenProviderPort;

    @MockitoBean
    private SignupUseCase signupUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private BulkImportBatchUseCase bulkImportBatchUseCase;

    private UUID orgId;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void publicEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointsWithoutTokenShouldReturn401Or403() throws Exception {
        mockMvc.perform(get("/api/v1/import-batches"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void protectedEndpointWithInsufficientRoleShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/import-batches"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void protectedEndpointWithRequiredRoleShouldSucceed() throws Exception {
        when(bulkImportBatchUseCase.listBatches(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/import-batches"))
                .andExpect(status().isOk());
    }
}
