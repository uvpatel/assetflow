package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetCategoryUseCase;
import com.assetflow.asset.domain.AssetCategory;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class AssetCategoryController {

    private final AssetCategoryUseCase categoryUseCase;

    public AssetCategoryController(AssetCategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    public record CreateCategoryRequest(
            @NotBlank(message = "Category name is required") String name,
            @NotBlank(message = "Category code is required") String code,
            String description
    ) {}

    @PostMapping
    public ResponseEntity<AssetCategory> createCategory(@RequestBody CreateCategoryRequest request) {
        AssetCategory category = categoryUseCase.createCategory(request.name(), request.code(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping
    public ResponseEntity<List<AssetCategory>> listCategories() {
        return ResponseEntity.ok(categoryUseCase.listCategories());
    }
}
