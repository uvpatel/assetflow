package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetBarcode;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssetBarcodeUseCase {

    public AssetBarcode generateBarcode(UUID organizationId, UUID assetId, String assetTag) {
        return AssetBarcode.generateForAsset(organizationId, assetId, assetTag);
    }
}
