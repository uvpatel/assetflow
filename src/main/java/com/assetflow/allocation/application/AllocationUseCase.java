package com.assetflow.allocation.application;

import com.assetflow.allocation.api.dto.AllocateAssetRequest;
import com.assetflow.allocation.api.dto.ReturnAssetRequest;
import com.assetflow.allocation.domain.AssetAllocation;
import com.assetflow.allocation.domain.AssetAllocationRepository;
import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.error.ConcurrencyConflictException;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AllocationUseCase {

    private final AssetAllocationRepository allocationRepository;
    private final AssetRepository assetRepository;

    public AllocationUseCase(AssetAllocationRepository allocationRepository, AssetRepository assetRepository) {
        this.allocationRepository = allocationRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AssetAllocation allocateAsset(AllocateAssetRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        allocationRepository.findActiveByAssetId(request.getAssetId()).ifPresent(active -> {
            throw new ConcurrencyConflictException("Asset " + request.getAssetId() + " is already active allocated.");
        });

        asset.setStatus(AssetStatus.ALLOCATED);
        assetRepository.save(asset);

        AssetAllocation allocation = new AssetAllocation(
                organizationId,
                request.getAssetId(),
                request.getAllocatedToEmployeeId(),
                request.getAllocatedToDepartmentId(),
                request.getExpectedReturnAt(),
                request.getNotes()
        );

        return allocationRepository.save(allocation);
    }

    @Transactional
    public AssetAllocation returnAsset(UUID allocationId, ReturnAssetRequest request) {
        AssetAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetAllocation", allocationId));

        allocation.markReturned(request.getReturnConditionState());
        AssetAllocation saved = allocationRepository.save(allocation);

        Asset asset = assetRepository.findById(allocation.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", allocation.getAssetId()));

        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setConditionState(request.getReturnConditionState());
        assetRepository.save(asset);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<AssetAllocation> listAllocations() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return allocationRepository.findByOrganizationId(organizationId);
    }
}
