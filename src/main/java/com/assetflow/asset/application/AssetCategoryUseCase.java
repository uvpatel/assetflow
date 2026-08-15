package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetCategory;
import com.assetflow.asset.domain.AssetCategoryRepository;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssetCategoryUseCase {

    private final AssetCategoryRepository categoryRepository;

    public AssetCategoryUseCase(AssetCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public AssetCategory createCategory(String name, String code, String description) {
        UUID organizationId = TenantContext.requireOrganizationId();
        AssetCategory category = new AssetCategory(organizationId, name, code, description);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<AssetCategory> listCategories() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return categoryRepository.findByOrganizationId(organizationId);
    }
}
