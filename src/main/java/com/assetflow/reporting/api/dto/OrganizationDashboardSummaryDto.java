package com.assetflow.reporting.api.dto;

import java.util.UUID;

public record OrganizationDashboardSummaryDto(
        UUID organizationId,
        long totalAssets,
        long availableAssets,
        long allocatedAssets,
        long underMaintenanceAssets,
        long activeBookings,
        long pendingMaintenanceRequests,
        long openAuditCycles
) {}
