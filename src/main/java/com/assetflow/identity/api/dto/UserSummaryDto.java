package com.assetflow.identity.api.dto;

import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;

import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String email,
        UUID organizationId,
        UserRole role,
        UserStatus status
) {
    public static UserSummaryDto fromDomain(UserAccount domain) {
        return new UserSummaryDto(
                domain.getId(),
                domain.getEmail(),
                domain.getOrganizationId(),
                domain.getRole(),
                domain.getStatus()
        );
    }
}
