package com.assetflow.maintenance.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.maintenance.api.dto.CreateMaintenanceRequestDto;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceUseCase {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final AssetRepository assetRepository;

    public MaintenanceUseCase(MaintenanceRequestRepository maintenanceRepository, AssetRepository assetRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public MaintenanceRequest requestMaintenance(CreateMaintenanceRequestDto requestDto) {
        UUID organizationId = TenantContext.requireOrganizationId();

        if (assetRepository.findById(requestDto.getAssetId()).isEmpty()) {
            throw new ResourceNotFoundException("Asset", requestDto.getAssetId());
        }

        MaintenanceRequest request = new MaintenanceRequest(
                organizationId,
                requestDto.getAssetId(),
                requestDto.getRequestedByEmployeeId(),
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getPriority()
        );

        return maintenanceRepository.save(request);
    }

    @Transactional
    public MaintenanceRequest approveRequest(UUID requestId, UUID technicianId) {
        MaintenanceRequest request = maintenanceRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", requestId));

        request.approve(technicianId);

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
        assetRepository.save(asset);

        return maintenanceRepository.save(request);
    }

    @Transactional
    public MaintenanceRequest resolveRequest(UUID requestId, String workNotes) {
        MaintenanceRequest request = maintenanceRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", requestId));

        request.resolve(workNotes);

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        return maintenanceRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRequest> listRequests() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return maintenanceRepository.findByOrganizationId(organizationId);
    }
}
