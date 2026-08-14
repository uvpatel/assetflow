package com.assetflow.notification.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateNotificationRequest {

    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "message must not be blank")
    private String message;

    private String type = "INFO";

    public CreateNotificationRequest() {
    }

    public CreateNotificationRequest(UUID userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
