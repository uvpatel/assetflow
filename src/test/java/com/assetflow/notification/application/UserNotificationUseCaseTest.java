package com.assetflow.notification.application;

import com.assetflow.notification.domain.NotificationPreference;
import com.assetflow.notification.domain.NotificationPreferenceRepository;
import com.assetflow.notification.domain.NotificationQueueMessage;
import com.assetflow.notification.domain.NotificationQueueMessageRepository;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @Mock
    private NotificationQueueMessageRepository queueRepository;

    private UserNotificationUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new UserNotificationUseCase(notificationRepository, preferenceRepository, queueRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should evaluate user channel preferences and queue multi-channel messages")
    void shouldEvaluatePreferencesAndQueueMessages() {
        UUID userId = UUID.randomUUID();
        NotificationPreference pref = new NotificationPreference(
                orgId, userId, "CUSTODY_TRANSFER", true, true, false, true
        );

        when(preferenceRepository.findByOrganizationIdAndUserIdAndNotificationType(orgId, userId, "CUSTODY_TRANSFER"))
                .thenReturn(Optional.of(pref));
        when(queueRepository.save(any(NotificationQueueMessage.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<NotificationQueueMessage> queued = useCase.dispatchMultiChannelNotification(
                userId,
                "CUSTODY_TRANSFER",
                "Asset Handover Pending",
                "Laptop transfer signature required"
        );

        // Expect 3 queued messages: IN_APP, EMAIL, SLACK (SMS disabled)
        assertThat(queued).hasSize(3);
        assertThat(queued.stream().map(NotificationQueueMessage::getChannel)).containsExactlyInAnyOrder("IN_APP", "EMAIL", "SLACK");
        verify(queueRepository, times(3)).save(any(NotificationQueueMessage.class));
    }
}
