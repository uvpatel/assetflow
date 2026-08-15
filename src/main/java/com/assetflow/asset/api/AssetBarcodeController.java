package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetBarcodeUseCase;
import com.assetflow.asset.domain.AssetBarcode;
import com.assetflow.shared.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetBarcodeController {

    private final AssetBarcodeUseCase useCase;

    public AssetBarcodeController(AssetBarcodeUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{assetId}/barcode")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER', 'AUDITOR')")
    public ResponseEntity<AssetBarcode> getAssetBarcode(@PathVariable UUID assetId, @RequestParam String tag) {
        UUID orgId = TenantContext.requireOrganizationId();
        return ResponseEntity.ok(useCase.generateBarcode(orgId, assetId, tag));
    }
}
