package com.assetflow.asset.api;

import com.assetflow.asset.api.dto.RegisterAssetRequest;
import com.assetflow.asset.application.AssetUseCase;
import com.assetflow.asset.domain.Asset;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetUseCase assetUseCase;

    public AssetController(AssetUseCase assetUseCase) {
        this.assetUseCase = assetUseCase;
    }

    @PostMapping
    public ResponseEntity<Asset> registerAsset(@Valid @RequestBody RegisterAssetRequest request) {
        Asset asset = assetUseCase.registerAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(asset);
    }

    @GetMapping
    public ResponseEntity<List<Asset>> listAssets() {
        return ResponseEntity.ok(assetUseCase.listAssets());
    }
}
