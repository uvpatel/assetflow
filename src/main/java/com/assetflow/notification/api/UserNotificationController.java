package com.assetflow.notification.api;

import com.assetflow.notification.api.dto.CreateNotificationRequest;
import com.assetflow.notification.api.dto.UpdateNotificationPreferenceRequest;
import com.assetflow.notification.application.UserNotificationUseCase;
import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationPreference;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-notifications")
public class UserNotificationController {

    private final UserNotificationUseCase userNotificationUseCase;

    public UserNotificationController(UserNotificationUseCase userNotificationUseCase) {
        this.userNotificationUseCase = userNotificationUseCase;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        Notification notification = userNotificationUseCase.createNotification(
                request.getUserId(),
                request.getTitle(),
                request.getMessage(),
                request.getType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(userNotificationUseCase.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(userNotificationUseCase.getUnreadUserNotifications(userId));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID notificationId) {
        Notification notification = userNotificationUseCase.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/preferences/user/{userId}")
    public ResponseEntity<List<NotificationPreference>> getUserPreferences(@PathVariable UUID userId) {
        return ResponseEntity.ok(userNotificationUseCase.getUserPreferences(userId));
    }

    @PutMapping("/preferences/user/{userId}")
    public ResponseEntity<NotificationPreference> updatePreference(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateNotificationPreferenceRequest request) {
        NotificationPreference preference = userNotificationUseCase.updatePreference(
                userId,
                request.getNotificationType(),
                request.isEmailEnabled(),
                request.isInAppEnabled()
        );
        return ResponseEntity.ok(preference);
    }
}
