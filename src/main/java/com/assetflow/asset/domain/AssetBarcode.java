package com.assetflow.asset.domain;

import java.util.Objects;
import java.util.UUID;

public class AssetBarcode {
    private final UUID assetId;
    private final String barcodePayload;
    private final String qrCodePayload;

    public AssetBarcode(UUID assetId, String barcodePayload, String qrCodePayload) {
        this.assetId = Objects.requireNonNull(assetId, "Asset ID cannot be null");
        this.barcodePayload = Objects.requireNonNull(barcodePayload, "Barcode payload cannot be null");
        this.qrCodePayload = Objects.requireNonNull(qrCodePayload, "QR code payload cannot be null");
    }

    public static AssetBarcode generateForAsset(UUID organizationId, UUID assetId, String assetTag) {
        String barcode = "AST-" + assetTag.toUpperCase();
        String qrCode = String.format("assetflow://org/%s/asset/%s?tag=%s", organizationId, assetId, assetTag);
        return new AssetBarcode(assetId, barcode, qrCode);
    }

    public UUID getAssetId() { return assetId; }
    public String getBarcodePayload() { return barcodePayload; }
    public String getQrCodePayload() { return qrCodePayload; }
}
