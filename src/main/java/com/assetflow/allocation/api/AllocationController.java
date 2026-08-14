package com.assetflow.allocation.api;

import com.assetflow.allocation.api.dto.AllocateAssetRequest;
import com.assetflow.allocation.api.dto.ReturnAssetRequest;
import com.assetflow.allocation.application.AllocationUseCase;
import com.assetflow.allocation.domain.AssetAllocation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/allocations")
public class AllocationController {

    private final AllocationUseCase allocationUseCase;

    public AllocationController(AllocationUseCase allocationUseCase) {
        this.allocationUseCase = allocationUseCase;
    }

    @PostMapping
    public ResponseEntity<AssetAllocation> allocateAsset(@Valid @RequestBody AllocateAssetRequest request) {
        AssetAllocation allocation = allocationUseCase.allocateAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(allocation);
    }

    @PostMapping("/{allocationId}/return")
    public ResponseEntity<AssetAllocation> returnAsset(
            @PathVariable UUID allocationId,
            @Valid @RequestBody ReturnAssetRequest request) {
        AssetAllocation allocation = allocationUseCase.returnAsset(allocationId, request);
        return ResponseEntity.ok(allocation);
    }

    @GetMapping
    public ResponseEntity<List<AssetAllocation>> listAllocations() {
        return ResponseEntity.ok(allocationUseCase.listAllocations());
    }
}
