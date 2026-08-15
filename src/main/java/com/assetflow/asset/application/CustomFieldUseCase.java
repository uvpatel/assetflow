package com.assetflow.asset.application;

import com.assetflow.asset.api.dto.CreateCategoryFieldDefinitionRequest;
import com.assetflow.asset.api.dto.SetAssetCustomFieldValueRequest;
import com.assetflow.asset.domain.*;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomFieldUseCase {

    private final CategoryFieldDefinitionRepository fieldDefinitionRepository;
    private final AssetCustomFieldValueRepository fieldValueRepository;
    private final AssetCategoryRepository categoryRepository;
    private final AssetRepository assetRepository;

    public CustomFieldUseCase(CategoryFieldDefinitionRepository fieldDefinitionRepository,
                              AssetCustomFieldValueRepository fieldValueRepository,
                              AssetCategoryRepository categoryRepository,
                              AssetRepository assetRepository) {
        this.fieldDefinitionRepository = fieldDefinitionRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.categoryRepository = categoryRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public CategoryFieldDefinition createFieldDefinition(CreateCategoryFieldDefinitionRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        categoryRepository.findById(request.getCategoryId())
                .filter(cat -> cat.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("AssetCategory", request.getCategoryId()));

        CategoryFieldDefinition definition = new CategoryFieldDefinition(
                organizationId,
                request.getCategoryId(),
                request.getFieldName(),
                request.getFieldLabel(),
                request.getFieldType(),
                request.isRequired(),
                request.getOptions()
        );

        return fieldDefinitionRepository.save(definition);
    }

    @Transactional(readOnly = true)
    public List<CategoryFieldDefinition> getFieldDefinitions(UUID categoryId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return fieldDefinitionRepository.findByOrganizationIdAndCategoryId(organizationId, categoryId);
    }

    @Transactional
    public List<AssetCustomFieldValue> setCustomFieldValues(UUID assetId, List<SetAssetCustomFieldValueRequest> requests) {
        UUID organizationId = TenantContext.requireOrganizationId();

        if (assetRepository.findById(assetId).isEmpty()) {
            throw new ResourceNotFoundException("Asset", assetId);
        }

        List<AssetCustomFieldValue> result = new ArrayList<>();
        for (SetAssetCustomFieldValueRequest req : requests) {
            if (!fieldDefinitionRepository.findById(req.getFieldDefinitionId()).isPresent()) {
                throw new ResourceNotFoundException("CategoryFieldDefinition", req.getFieldDefinitionId());
            }

            Optional<AssetCustomFieldValue> existing = fieldValueRepository.findByAssetIdAndFieldDefinitionId(assetId, req.getFieldDefinitionId());
            AssetCustomFieldValue val;
            if (existing.isPresent()) {
                val = existing.get();
                val.setFieldValue(req.getFieldValue());
            } else {
                val = new AssetCustomFieldValue(organizationId, assetId, req.getFieldDefinitionId(), req.getFieldValue());
            }
            result.add(fieldValueRepository.save(val));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<AssetCustomFieldValue> getCustomFieldValues(UUID assetId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        return fieldValueRepository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
