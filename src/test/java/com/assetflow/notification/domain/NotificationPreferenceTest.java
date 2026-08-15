package com.assetflow.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceTest {

    @Test
    @DisplayName("Should initialize notification preference with default channels")
    void shouldInitializeNotificationPreference() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        NotificationPreference pref = new NotificationPreference(orgId, userId, "MAINTENANCE_SLA", true, true, false, true);

        assertThat(pref.getOrganizationId()).isEqualTo(orgId);
        assertThat(pref.getUserId()).isEqualTo(userId);
        assertThat(pref.getNotificationType()).isEqualTo("MAINTENANCE_SLA");
        assertThat(pref.isEmailEnabled()).isTrue();
        assertThat(pref.isInAppEnabled()).isTrue();
        assertThat(pref.isSmsEnabled()).isFalse();
        assertThat(pref.isSlackEnabled()).isTrue();
    }
}
