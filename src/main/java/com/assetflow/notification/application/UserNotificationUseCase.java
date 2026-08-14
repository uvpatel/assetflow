package com.assetflow.notification.application;

import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationPreferenceRepository;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public UserNotificationUseCase(NotificationRepository notificationRepository,
                                   NotificationPreferenceRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public Notification createNotification(UUID userId, String title, String message, String type) {
        UUID organizationId = TenantContext.requireOrganizationId();

        String notificationType = (type != null && !type.isBlank()) ? type : "INFO";
        Optional<NotificationPreference> pref = preferenceRepository
                .findByOrganizationIdAndUserIdAndNotificationType(organizationId, userId, notificationType);

        Notification notification = new Notification(organizationId, userId, title, message, notificationType);
        if (pref.isPresent() && !pref.get().isInAppEnabled()) {
            notification.setRead(true);
        }

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(UUID userId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return notificationRepository.findByOrganizationIdAndUserId(organizationId, userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadUserNotifications(UUID userId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return notificationRepository.findUnreadByOrganizationIdAndUserId(organizationId, userId);
    }

    @Transactional
    public Notification markAsRead(UUID notificationId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Transactional
    public NotificationPreference updatePreference(UUID userId, String notificationType, boolean emailEnabled, boolean inAppEnabled) {
        UUID organizationId = TenantContext.requireOrganizationId();
        String type = (notificationType != null && !notificationType.isBlank()) ? notificationType : "ALL";

        NotificationPreference preference = preferenceRepository
                .findByOrganizationIdAndUserIdAndNotificationType(organizationId, userId, type)
                .orElseGet(() -> new NotificationPreference(organizationId, userId, type, emailEnabled, inAppEnabled));

        preference.setEmailEnabled(emailEnabled);
        preference.setInAppEnabled(inAppEnabled);
        return preferenceRepository.save(preference);
    }

    @Transactional(readOnly = true)
    public List<NotificationPreference> getUserPreferences(UUID userId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return preferenceRepository.findByOrganizationIdAndUserId(organizationId, userId);
    }
}
