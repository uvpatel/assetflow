package com.assetflow.notification.api;

import com.assetflow.notification.api.dto.CreateNotificationRequest;
import com.assetflow.notification.api.dto.UpdateNotificationPreferenceRequest;
import com.assetflow.notification.application.UserNotificationUseCase;
import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationQueueMessage;
import com.assetflow.shared.error.GlobalExceptionHandler;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserNotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserNotificationUseCase userNotificationUseCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create user notification via POST")
    void shouldCreateNotification() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification(
                orgId,
                userId,
                "Maintenance Overdue",
                "Asset MacBook Pro maintenance is overdue",
                "ALERT"
        );

        when(userNotificationUseCase.createNotification(
                eq(userId), eq("Maintenance Overdue"), eq("Asset MacBook Pro maintenance is overdue"), eq("ALERT")
        )).thenReturn(notification);

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(userId);
        request.setTitle("Maintenance Overdue");
        request.setMessage("Asset MacBook Pro maintenance is overdue");
        request.setType("ALERT");

        mockMvc.perform(post("/user-notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.title").value("Maintenance Overdue"))
                .andExpect(jsonPath("$.type").value("ALERT"));
    }

    @Test
    @DisplayName("Should dispatch multi-channel notification via POST")
    void shouldDispatchNotification() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID recipientUserId = UUID.randomUUID();
        NotificationQueueMessage msg = new NotificationQueueMessage(
                orgId,
                recipientUserId,
                "EMAIL",
                "MAINTENANCE_DUE",
                "Maintenance Reminder",
                "Check vehicle oil level"
        );

        when(userNotificationUseCase.dispatchMultiChannelNotification(
                eq(recipientUserId), eq("MAINTENANCE_DUE"), eq("Maintenance Reminder"), eq("Check vehicle oil level")
        )).thenReturn(List.of(msg));

        UserNotificationController.DispatchNotificationRequest request =
                new UserNotificationController.DispatchNotificationRequest(
                        recipientUserId,
                        "MAINTENANCE_DUE",
                        "Maintenance Reminder",
                        "Check vehicle oil level"
                );

        mockMvc.perform(post("/user-notifications/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channel").value("EMAIL"))
                .andExpect(jsonPath("$[0].subject").value("Maintenance Reminder"));
    }

    @Test
    @DisplayName("Should fetch unread user notifications via GET")
    void shouldGetUnreadNotifications() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification(
                orgId,
                userId,
                "Audit Scheduled",
                "Annual IT Audit starts tomorrow",
                "INFO"
        );

        when(userNotificationUseCase.getUnreadUserNotifications(eq(userId))).thenReturn(List.of(notification));

        mockMvc.perform(get("/user-notifications/user/{userId}/unread", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Audit Scheduled"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @DisplayName("Should mark notification as read via POST")
    void shouldMarkAsRead() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID notifId = UUID.randomUUID();
        Notification notification = new Notification(
                orgId,
                UUID.randomUUID(),
                "Audit Scheduled",
                "Annual IT Audit starts tomorrow",
                "INFO"
        );
        notification.markAsRead();

        when(userNotificationUseCase.markAsRead(eq(notifId))).thenReturn(notification);

        mockMvc.perform(post("/user-notifications/{notificationId}/read", notifId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    @DisplayName("Should update user notification preferences via PUT")
    void shouldUpdatePreferences() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        NotificationPreference pref = new NotificationPreference(
                orgId,
                userId,
                "MAINTENANCE_DUE",
                true,
                true,
                false,
                true
        );

        when(userNotificationUseCase.updatePreference(
                eq(userId), eq("MAINTENANCE_DUE"), eq(true), eq(true), eq(false), eq(true)
        )).thenReturn(pref);

        UpdateNotificationPreferenceRequest request = new UpdateNotificationPreferenceRequest();
        request.setNotificationType("MAINTENANCE_DUE");
        request.setEmailEnabled(true);
        request.setInAppEnabled(true);
        request.setSmsEnabled(false);
        request.setSlackEnabled(true);

        mockMvc.perform(put("/user-notifications/preferences/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationType").value("MAINTENANCE_DUE"))
                .andExpect(jsonPath("$.emailEnabled").value(true))
                .andExpect(jsonPath("$.slackEnabled").value(true));
    }
}
