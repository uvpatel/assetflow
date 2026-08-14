package com.assetflow.asset.api;

import com.assetflow.asset.api.dto.CreateCategoryFieldDefinitionRequest;
import com.assetflow.asset.api.dto.SetAssetCustomFieldValueRequest;
import com.assetflow.asset.application.CustomFieldUseCase;
import com.assetflow.asset.domain.AssetCustomFieldValue;
import com.assetflow.asset.domain.CategoryFieldDefinition;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/custom-fields")
public class CustomFieldController {

    private final CustomFieldUseCase customFieldUseCase;

    public CustomFieldController(CustomFieldUseCase customFieldUseCase) {
        this.customFieldUseCase = customFieldUseCase;
    }

    @PostMapping("/definitions")
    public ResponseEntity<CategoryFieldDefinition> createFieldDefinition(@Valid @RequestBody CreateCategoryFieldDefinitionRequest request) {
        CategoryFieldDefinition definition = customFieldUseCase.createFieldDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(definition);
    }

    @GetMapping("/definitions/category/{categoryId}")
    public ResponseEntity<List<CategoryFieldDefinition>> getFieldDefinitions(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(customFieldUseCase.getFieldDefinitions(categoryId));
    }

    @PostMapping("/values/asset/{assetId}")
    public ResponseEntity<List<AssetCustomFieldValue>> setCustomFieldValues(
            @PathVariable UUID assetId,
            @Valid @RequestBody List<SetAssetCustomFieldValueRequest> requests) {
        return ResponseEntity.ok(customFieldUseCase.setCustomFieldValues(assetId, requests));
    }

    @GetMapping("/values/asset/{assetId}")
    public ResponseEntity<List<AssetCustomFieldValue>> getCustomFieldValues(@PathVariable UUID assetId) {
        return ResponseEntity.ok(customFieldUseCase.getCustomFieldValues(assetId));
    }
}
