package com.assetflow.asset.infrastructure;

import com.assetflow.asset.domain.CategoryFieldDefinition;
import com.assetflow.asset.domain.CategoryFieldDefinitionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataCategoryFieldDefinitionRepository extends JpaRepository<CategoryFieldDefinition, UUID> {
    List<CategoryFieldDefinition> findByOrganizationIdAndCategoryId(UUID organizationId, UUID categoryId);
}

@Repository
public class JpaCategoryFieldDefinitionRepositoryAdapter implements CategoryFieldDefinitionRepository {

    private final SpringDataCategoryFieldDefinitionRepository repository;

    public JpaCategoryFieldDefinitionRepositoryAdapter(SpringDataCategoryFieldDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public CategoryFieldDefinition save(CategoryFieldDefinition definition) {
        return repository.save(definition);
    }

    @Override
    public Optional<CategoryFieldDefinition> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<CategoryFieldDefinition> findByOrganizationIdAndCategoryId(UUID organizationId, UUID categoryId) {
        return repository.findByOrganizationIdAndCategoryId(organizationId, categoryId);
    }
}
