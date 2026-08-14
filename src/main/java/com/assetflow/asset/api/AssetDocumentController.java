package com.assetflow.asset.api;

import com.assetflow.asset.api.dto.RegisterAssetDocumentRequest;
import com.assetflow.asset.application.AssetDocumentUseCase;
import com.assetflow.asset.domain.AssetDocument;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assets")
public class AssetDocumentController {

    private final AssetDocumentUseCase assetDocumentUseCase;

    public AssetDocumentController(AssetDocumentUseCase assetDocumentUseCase) {
        this.assetDocumentUseCase = assetDocumentUseCase;
    }

    @PostMapping("/documents")
    public ResponseEntity<AssetDocument> registerDocument(@Valid @RequestBody RegisterAssetDocumentRequest request) {
        AssetDocument document = assetDocumentUseCase.registerDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/{assetId}/documents")
    public ResponseEntity<List<AssetDocument>> listDocuments(@PathVariable UUID assetId) {
        return ResponseEntity.ok(assetDocumentUseCase.listDocuments(assetId));
    }
}
