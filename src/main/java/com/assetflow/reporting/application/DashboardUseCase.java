package com.assetflow.reporting.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.booking.domain.ResourceBooking;
import com.assetflow.booking.domain.ResourceBookingRepository;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.reporting.api.dto.OrganizationDashboardSummaryDto;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DashboardUseCase {

    private final AssetRepository assetRepository;
    private final ResourceBookingRepository bookingRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final AuditCycleRepository auditCycleRepository;

    public DashboardUseCase(AssetRepository assetRepository,
                            ResourceBookingRepository bookingRepository,
                            MaintenanceRequestRepository maintenanceRepository,
                            AuditCycleRepository auditCycleRepository) {
        this.assetRepository = assetRepository;
        this.bookingRepository = bookingRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.auditCycleRepository = auditCycleRepository;
    }

    @Transactional(readOnly = true)
    public OrganizationDashboardSummaryDto getDashboardSummary() {
        UUID organizationId = TenantContext.requireOrganizationId();

        List<Asset> assets = assetRepository.findByOrganizationId(organizationId);
        long totalAssets = assets.size();
        long availableAssets = assets.stream().filter(a -> a.getStatus() == AssetStatus.AVAILABLE).count();
        long allocatedAssets = assets.stream().filter(a -> a.getStatus() == AssetStatus.ALLOCATED).count();
        long underMaintenance = assets.stream().filter(a -> a.getStatus() == AssetStatus.UNDER_MAINTENANCE).count();

        List<ResourceBooking> bookings = bookingRepository.findByOrganizationId(organizationId);
        long activeBookings = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();

        List<MaintenanceRequest> maintenanceRequests = maintenanceRepository.findByOrganizationId(organizationId);
        long pendingMaintenance = maintenanceRequests.stream().filter(m -> "PENDING".equals(m.getStatus())).count();

        List<AuditCycle> cycles = auditCycleRepository.findByOrganizationId(organizationId);
        long openCycles = cycles.stream().filter(c -> !c.isClosed()).count();

        return new OrganizationDashboardSummaryDto(
                organizationId,
                totalAssets,
                availableAssets,
                allocatedAssets,
                underMaintenance,
                activeBookings,
                pendingMaintenance,
                openCycles
        );
    }
}
