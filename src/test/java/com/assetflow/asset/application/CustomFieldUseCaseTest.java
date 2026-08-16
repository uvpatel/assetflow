package com.assetflow.asset.application;

import com.assetflow.asset.api.dto.SetAssetCustomFieldValueRequest;
import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetCustomFieldValue;
import com.assetflow.asset.domain.AssetCustomFieldValueRepository;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.CategoryFieldDefinition;
import com.assetflow.asset.domain.CategoryFieldDefinitionRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFieldUseCaseTest {

    @Mock
    private CategoryFieldDefinitionRepository fieldDefinitionRepository;

    @Mock
    private AssetCustomFieldValueRepository fieldValueRepository;

    @Mock
    private com.assetflow.asset.domain.AssetCategoryRepository categoryRepository;

    @Mock
    private AssetRepository assetRepository;

    private CustomFieldUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CustomFieldUseCase(fieldDefinitionRepository, fieldValueRepository, categoryRepository, assetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should validate custom field value pattern before persisting asset custom field value")
    void shouldValidateAndSetCustomFieldValue() {
        UUID assetId = UUID.randomUUID();
        UUID fieldDefId = UUID.randomUUID();

        Asset asset = new Asset(orgId, UUID.randomUUID(), "ASSET-01", "MacBook Pro", "Laptop", "SN-999", "HQ");
        CategoryFieldDefinition def = new CategoryFieldDefinition(
                orgId, UUID.randomUUID(), "ram_gb", "RAM (GB)", "NUMBER", true, null, null, null, new BigDecimal("8"), new BigDecimal("128")
        );

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(fieldDefinitionRepository.findById(fieldDefId)).thenReturn(Optional.of(def));
        when(fieldValueRepository.findByAssetIdAndFieldDefinitionId(assetId, fieldDefId)).thenReturn(Optional.empty());
        when(fieldValueRepository.save(any(AssetCustomFieldValue.class))).thenAnswer(inv -> inv.getArgument(0));

        SetAssetCustomFieldValueRequest req = new SetAssetCustomFieldValueRequest(fieldDefId, "32");
        List<AssetCustomFieldValue> results = useCase.setCustomFieldValues(assetId, List.of(req));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFieldValue()).isEqualTo("32");
        verify(fieldValueRepository).save(any(AssetCustomFieldValue.class));
    }

    @Test
    @DisplayName("Should reject invalid custom field value violating validation rules")
    void shouldRejectInvalidCustomFieldValue() {
        UUID assetId = UUID.randomUUID();
        UUID fieldDefId = UUID.randomUUID();

        Asset asset = new Asset(orgId, UUID.randomUUID(), "ASSET-02", "Server Rack", "Hardware", "SN-888", "DataCenter");
        CategoryFieldDefinition def = new CategoryFieldDefinition(
                orgId, UUID.randomUUID(), "ram_gb", "RAM (GB)", "NUMBER", true, null, null, null, new BigDecimal("8"), new BigDecimal("128")
        );

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(fieldDefinitionRepository.findById(fieldDefId)).thenReturn(Optional.of(def));

        SetAssetCustomFieldValueRequest req = new SetAssetCustomFieldValueRequest(fieldDefId, "4");

        assertThatThrownBy(() -> useCase.setCustomFieldValues(assetId, List.of(req)))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("cannot be less than 8");
    }
}
