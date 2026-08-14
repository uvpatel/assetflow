package com.assetflow.identity.api;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.LoginRequest;
import com.assetflow.identity.api.dto.RefreshTokenRequest;
import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.api.dto.TokenRefreshResponse;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.LoginUseCase;
import com.assetflow.identity.application.LogoutUseCase;
import com.assetflow.identity.application.RefreshTokenUseCase;
import com.assetflow.identity.application.SignupUseCase;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import com.assetflow.identity.infrastructure.security.JwtTokenProvider;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SignupUseCase signupUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldReturn201OnSuccessfulSignup() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("user@example.com", "password123", orgId, null);
        UserSummaryDto response = new UserSummaryDto(userId, "user@example.com", orgId, UserRole.EMPLOYEE, UserStatus.ACTIVE);

        when(signupUseCase.execute(any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void shouldReturn400WhenRoleElevationAttempted() throws Exception {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("admin@example.com", "password123", orgId, "ADMIN");

        when(signupUseCase.execute(any(SignupRequest.class)))
                .thenThrow(new DomainViolationException("Role Elevation Security", "Public signup does not allow specifying custom roles. New users are assigned EMPLOYEE role by default."));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Role Elevation Security"))
                .andExpect(jsonPath("$.detail").value("Public signup does not allow specifying custom roles. New users are assigned EMPLOYEE role by default."));
    }

    @Test
    void shouldReturn200OnSuccessfulLogin() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        UserSummaryDto userDto = new UserSummaryDto(userId, "user@example.com", orgId, UserRole.EMPLOYEE, UserStatus.ACTIVE);
        AuthResponse response = new AuthResponse("accessTokenVal", "refreshTokenVal", 900, userDto);

        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessTokenVal"))
                .andExpect(jsonPath("$.refreshToken").value("refreshTokenVal"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void shouldReturn200OnSuccessfulRefresh() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("rawRefreshToken");
        TokenRefreshResponse response = new TokenRefreshResponse("newAccess", "newRefresh", 900);

        when(refreshTokenUseCase.execute(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccess"))
                .andExpect(jsonPath("$.refreshToken").value("newRefresh"));
    }

    @Test
    void shouldReturn204OnLogout() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("rawRefreshToken");
        doNothing().when(logoutUseCase).execute(any(RefreshTokenRequest.class));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
