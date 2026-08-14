package com.assetflow.notification.api.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateNotificationPreferenceRequest {

    @NotBlank(message = "notificationType must not be blank")
    private String notificationType = "ALL";

    private boolean emailEnabled = true;

    private boolean inAppEnabled = true;

    public UpdateNotificationPreferenceRequest() {
    }

    public UpdateNotificationPreferenceRequest(String notificationType, boolean emailEnabled, boolean inAppEnabled) {
        this.notificationType = notificationType;
        this.emailEnabled = emailEnabled;
        this.inAppEnabled = inAppEnabled;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public void setInAppEnabled(boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }
}
