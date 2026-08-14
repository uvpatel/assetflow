package com.assetflow.asset.api;

import com.assetflow.asset.application.QrCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/assets")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/{assetId}/qr-code")
    public ResponseEntity<Map<String, String>> getQrCodePayload(@PathVariable UUID assetId) {
        String payload = qrCodeService.generateQrCodePayload(assetId);
        return ResponseEntity.ok(Map.of("qrPayload", payload));
    }
}
