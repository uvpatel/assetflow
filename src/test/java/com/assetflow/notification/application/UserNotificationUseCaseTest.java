package com.assetflow.notification.application;

import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationPreferenceRepository;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNotificationUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private UserNotificationUseCase userNotificationUseCase;

    private UUID orgId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createNotification_success() {
        when(preferenceRepository.findByOrganizationIdAndUserIdAndNotificationType(orgId, userId, "ALERT"))
                .thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = userNotificationUseCase.createNotification(userId, "System Alert", "Maintenance scheduled", "ALERT");

        assertThat(result).isNotNull();
        assertThat(result.getOrganizationId()).isEqualTo(orgId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo("System Alert");
        assertThat(result.getMessage()).isEqualTo("Maintenance scheduled");
        assertThat(result.getType()).isEqualTo("ALERT");
        assertThat(result.isRead()).isFalse();
    }

    @Test
    void createNotification_disabledInApp_marksAsRead() {
        NotificationPreference pref = new NotificationPreference(orgId, userId, "ALERT", true, false);
        when(preferenceRepository.findByOrganizationIdAndUserIdAndNotificationType(orgId, userId, "ALERT"))
                .thenReturn(Optional.of(pref));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = userNotificationUseCase.createNotification(userId, "System Alert", "Maintenance scheduled", "ALERT");

        assertThat(result).isNotNull();
        assertThat(result.isRead()).isTrue();
    }

    @Test
    void getUserNotifications_success() {
        Notification notification = new Notification(orgId, userId, "Title", "Message", "INFO");
        when(notificationRepository.findByOrganizationIdAndUserId(orgId, userId)).thenReturn(List.of(notification));

        List<Notification> results = userNotificationUseCase.getUserNotifications(userId);

        assertThat(results).hasSize(1);
    }

    @Test
    void getUnreadUserNotifications_success() {
        Notification notification = new Notification(orgId, userId, "Title", "Message", "INFO");
        when(notificationRepository.findUnreadByOrganizationIdAndUserId(orgId, userId)).thenReturn(List.of(notification));

        List<Notification> results = userNotificationUseCase.getUnreadUserNotifications(userId);

        assertThat(results).hasSize(1);
    }

    @Test
    void markAsRead_success() {
        UUID notifId = UUID.randomUUID();
        Notification notification = new Notification(orgId, userId, "Title", "Message", "INFO");
        notification.setId(notifId);

        when(notificationRepository.findById(notifId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = userNotificationUseCase.markAsRead(notifId);

        assertThat(result.isRead()).isTrue();
    }

    @Test
    void markAsRead_notFound_throwsException() {
        UUID notifId = UUID.randomUUID();
        when(notificationRepository.findById(notifId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userNotificationUseCase.markAsRead(notifId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePreference_createsNewPreference() {
        when(preferenceRepository.findByOrganizationIdAndUserIdAndNotificationType(orgId, userId, "ALERT"))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(i -> i.getArgument(0));

        NotificationPreference result = userNotificationUseCase.updatePreference(userId, "ALERT", true, false);

        assertThat(result).isNotNull();
        assertThat(result.getOrganizationId()).isEqualTo(orgId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getNotificationType()).isEqualTo("ALERT");
        assertThat(result.isEmailEnabled()).isTrue();
        assertThat(result.isInAppEnabled()).isFalse();
    }

    @Test
    void getUserPreferences_success() {
        NotificationPreference pref = new NotificationPreference(orgId, userId, "ALL", true, true);
        when(preferenceRepository.findByOrganizationIdAndUserId(orgId, userId)).thenReturn(List.of(pref));

        List<NotificationPreference> results = userNotificationUseCase.getUserPreferences(userId);

        assertThat(results).hasSize(1);
    }
}
