package com.assetflow.allocation.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AcceptCustodyTransferRequest(
        @NotBlank(message = "Digital signature is required to accept custody transfer")
        String digitalSignature
) {}
