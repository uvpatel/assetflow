package com.assetflow.asset.api;

import com.assetflow.asset.api.dto.AssetScanVerificationResponse;
import com.assetflow.asset.application.AssetBarcodeUseCase;
import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetBarcode;
import com.assetflow.shared.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/scan")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER', 'AUDITOR')")
    public ResponseEntity<AssetScanVerificationResponse> scanAssetBarcode(@RequestParam("code") String rawCode) {
        Asset asset = useCase.resolveScannedAsset(rawCode);
        return ResponseEntity.ok(AssetScanVerificationResponse.fromDomain(asset));
    }
}
