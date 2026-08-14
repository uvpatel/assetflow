package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryFieldDefinitionRepository {
    CategoryFieldDefinition save(CategoryFieldDefinition definition);
    Optional<CategoryFieldDefinition> findById(UUID id);
    List<CategoryFieldDefinition> findByOrganizationIdAndCategoryId(UUID organizationId, UUID categoryId);
}
