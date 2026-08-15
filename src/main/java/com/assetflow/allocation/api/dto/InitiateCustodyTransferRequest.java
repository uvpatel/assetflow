package com.assetflow.allocation.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record InitiateCustodyTransferRequest(
        @NotNull(message = "Sender employee ID is required")
        UUID senderEmployeeId,

        @NotNull(message = "Recipient employee ID is required")
        UUID recipientEmployeeId,

        @NotEmpty(message = "At least one asset ID is required for custody transfer")
        List<UUID> assetIds,

        String notes
) {}
