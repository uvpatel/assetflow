package com.assetflow.notification.api.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateNotificationPreferenceRequest {

    @NotBlank(message = "notificationType must not be blank")
    private String notificationType = "ALL";

    private boolean emailEnabled = true;

    private boolean inAppEnabled = true;

    private boolean smsEnabled = false;

    private boolean slackEnabled = false;

    public UpdateNotificationPreferenceRequest() {
    }

    public UpdateNotificationPreferenceRequest(String notificationType, boolean emailEnabled, boolean inAppEnabled, boolean smsEnabled, boolean slackEnabled) {
        this.notificationType = notificationType;
        this.emailEnabled = emailEnabled;
        this.inAppEnabled = inAppEnabled;
        this.smsEnabled = smsEnabled;
        this.slackEnabled = slackEnabled;
    }

    public UpdateNotificationPreferenceRequest(String notificationType, boolean emailEnabled, boolean inAppEnabled) {
        this(notificationType, emailEnabled, inAppEnabled, false, false);
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

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isSlackEnabled() {
        return slackEnabled;
    }

    public void setSlackEnabled(boolean slackEnabled) {
        this.slackEnabled = slackEnabled;
    }
}
