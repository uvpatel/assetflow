package com.assetflow.asset.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetBarcode;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetBarcodeUseCaseTest {

    @Mock
    private AssetRepository assetRepository;

    private AssetBarcodeUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetBarcodeUseCase(assetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should generate valid barcode payloads for asset")
    void shouldGenerateBarcodePayloads() {
        UUID assetId = UUID.randomUUID();
        AssetBarcode barcode = useCase.generateBarcode(orgId, assetId, "LAPTOP-001");

        assertThat(barcode.getAssetId()).isEqualTo(assetId);
        assertThat(barcode.getBarcodePayload()).isEqualTo("AST-LAPTOP-001");
        assertThat(barcode.getQrCodePayload()).contains("LAPTOP-001");
    }

    @Test
    @DisplayName("Should successfully resolve scanned asset by barcode tag with prefix")
    void shouldResolveScannedAssetByTag() {
        UUID assetId = UUID.randomUUID();
        Asset asset = new Asset(orgId, UUID.randomUUID(), "MAC-001", "MacBook Pro", "Desc", "SN123", "Office 101");
        asset.setId(assetId);

        when(assetRepository.findByOrganizationIdAndAssetTag(orgId, "MAC-001")).thenReturn(Optional.of(asset));

        Asset resolved = useCase.resolveScannedAsset("AST-MAC-001");

        assertThat(resolved.getId()).isEqualTo(assetId);
        assertThat(resolved.getAssetTag()).isEqualTo("MAC-001");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when scanned code does not exist")
    void shouldThrowNotFoundForUnknownCode() {
        when(assetRepository.findByOrganizationIdAndAssetTag(orgId, "UNKNOWN-TAG")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.resolveScannedAsset("AST-UNKNOWN-TAG"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
