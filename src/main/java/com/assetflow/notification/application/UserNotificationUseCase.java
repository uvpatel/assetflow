package com.assetflow.notification.application;

import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationPreferenceRepository;
import com.assetflow.notification.domain.NotificationQueueMessage;
import com.assetflow.notification.domain.NotificationQueueMessageRepository;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(UserNotificationUseCase.class);

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationQueueMessageRepository queueRepository;

    public UserNotificationUseCase(NotificationRepository notificationRepository,
                                   NotificationPreferenceRepository preferenceRepository,
                                   NotificationQueueMessageRepository queueRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.queueRepository = queueRepository;
    }

    @Transactional
    public List<NotificationQueueMessage> dispatchMultiChannelNotification(UUID recipientUserId, String eventType, String subject, String body) {
        UUID organizationId = TenantContext.requireOrganizationId();
        String type = (eventType != null && !eventType.isBlank()) ? eventType : "ALL";

        Optional<NotificationPreference> pref = preferenceRepository
                .findByOrganizationIdAndUserIdAndNotificationType(organizationId, recipientUserId, type);

        boolean inApp = pref.map(NotificationPreference::isInAppEnabled).orElse(true);
        boolean email = pref.map(NotificationPreference::isEmailEnabled).orElse(true);
        boolean sms = pref.map(NotificationPreference::isSmsEnabled).orElse(false);
        boolean slack = pref.map(NotificationPreference::isSlackEnabled).orElse(false);

        List<NotificationQueueMessage> queuedMessages = new ArrayList<>();

        if (inApp) {
            Notification notification = new Notification(organizationId, recipientUserId, subject, body, type);
            notificationRepository.save(notification);
            queuedMessages.add(queueRepository.save(new NotificationQueueMessage(organizationId, recipientUserId, "IN_APP", type, subject, body)));
        }

        if (email) {
            queuedMessages.add(queueRepository.save(new NotificationQueueMessage(organizationId, recipientUserId, "EMAIL", type, subject, body)));
        }

        if (sms) {
            queuedMessages.add(queueRepository.save(new NotificationQueueMessage(organizationId, recipientUserId, "SMS", type, subject, body)));
        }

        if (slack) {
            queuedMessages.add(queueRepository.save(new NotificationQueueMessage(organizationId, recipientUserId, "SLACK", type, subject, body)));
        }

        log.info("Dispatched {} queued notification messages for recipient user {}", queuedMessages.size(), recipientUserId);
        return queuedMessages;
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
    public NotificationPreference updatePreference(UUID userId, String notificationType, boolean emailEnabled, boolean inAppEnabled, boolean smsEnabled, boolean slackEnabled) {
        UUID organizationId = TenantContext.requireOrganizationId();
        String type = (notificationType != null && !notificationType.isBlank()) ? notificationType : "ALL";

        NotificationPreference preference = preferenceRepository
                .findByOrganizationIdAndUserIdAndNotificationType(organizationId, userId, type)
                .orElseGet(() -> new NotificationPreference(organizationId, userId, type, emailEnabled, inAppEnabled, smsEnabled, slackEnabled));

        preference.setEmailEnabled(emailEnabled);
        preference.setInAppEnabled(inAppEnabled);
        preference.setSmsEnabled(smsEnabled);
        preference.setSlackEnabled(slackEnabled);
        return preferenceRepository.save(preference);
    }

    @Transactional
    public NotificationPreference updatePreference(UUID userId, String notificationType, boolean emailEnabled, boolean inAppEnabled) {
        return updatePreference(userId, notificationType, emailEnabled, inAppEnabled, false, false);
    }

    @Transactional(readOnly = true)
    public List<NotificationPreference> getUserPreferences(UUID userId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return preferenceRepository.findByOrganizationIdAndUserId(organizationId, userId);
    }
}
