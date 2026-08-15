package com.assetflow.identity.api;

import com.assetflow.identity.api.dto.DeviceSessionDto;
import com.assetflow.identity.application.GlobalLogoutUseCase;
import com.assetflow.identity.application.SessionManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/sessions")
public class SessionController {

    private final SessionManagementUseCase sessionManagementUseCase;
    private final GlobalLogoutUseCase globalLogoutUseCase;

    public SessionController(SessionManagementUseCase sessionManagementUseCase,
                             GlobalLogoutUseCase globalLogoutUseCase) {
        this.sessionManagementUseCase = sessionManagementUseCase;
        this.globalLogoutUseCase = globalLogoutUseCase;
    }

    @GetMapping
    public ResponseEntity<List<DeviceSessionDto>> getActiveSessions() {
        UUID currentUserId = resolveCurrentUserId();
        List<DeviceSessionDto> sessions = sessionManagementUseCase.listActiveSessions(currentUserId);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> revokeSession(@PathVariable UUID sessionId) {
        UUID currentUserId = resolveCurrentUserId();
        sessionManagementUseCase.revokeSession(sessionId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/global-logout")
    public ResponseEntity<Void> globalLogout() {
        UUID currentUserId = resolveCurrentUserId();
        globalLogoutUseCase.execute(currentUserId);
        return ResponseEntity.noContent().build();
    }

    private UUID resolveCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return UUID.fromString(auth.getName());
    }
}
