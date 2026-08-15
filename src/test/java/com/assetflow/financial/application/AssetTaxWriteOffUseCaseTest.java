package com.assetflow.financial.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.financial.domain.AssetTaxWriteOff;
import com.assetflow.financial.domain.AssetTaxWriteOffRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetTaxWriteOffUseCaseTest {

    @Mock
    private AssetTaxWriteOffRepository repository;

    @Mock
    private AssetRepository assetRepository;

    private AssetTaxWriteOffUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetTaxWriteOffUseCase(repository, assetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should auto-approve write-off under $10,000 threshold and update asset status if fully written off")
    void shouldAutoApproveSmallWriteOff() {
        UUID assetId = UUID.randomUUID();
        Asset asset = new Asset(orgId, UUID.randomUUID(), "TAG-1", "Laptop", "Desc", "SN1", "Loc1");
        asset.setId(assetId);

        when(repository.save(any(AssetTaxWriteOff.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        AssetTaxWriteOff writeOff = useCase.executeWriteOff(
                orgId,
                assetId,
                2026,
                new BigDecimal("5000.00"),
                new BigDecimal("5000.00"),
                "Complete obsolescence"
        );

        assertThat(writeOff.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.APPROVED);
        assertThat(writeOff.getRemainingTaxBasis()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(asset.getStatus()).isEqualTo(AssetStatus.DISPOSED);
    }

    @Test
    @DisplayName("Should require approval for write-offs exceeding $10,000 threshold")
    void shouldRequireApprovalForLargeWriteOff() {
        UUID assetId = UUID.randomUUID();

        when(repository.save(any(AssetTaxWriteOff.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetTaxWriteOff writeOff = useCase.executeWriteOff(
                orgId,
                assetId,
                2026,
                new BigDecimal("50000.00"),
                new BigDecimal("25000.00"),
                "Major server rack damage"
        );

        assertThat(writeOff.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.PENDING_APPROVAL);
    }

    @Test
    @DisplayName("Should approve pending write-off and dispose asset upon 100% write-off")
    void shouldApproveWriteOffAndDisposeAsset() {
        UUID writeOffId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        Asset asset = new Asset(orgId, UUID.randomUUID(), "TAG-2", "Server", "Desc", "SN2", "Datacenter");
        asset.setId(assetId);

        AssetTaxWriteOff writeOff = new AssetTaxWriteOff(
                writeOffId,
                orgId,
                assetId,
                2026,
                new BigDecimal("25000.00"),
                BigDecimal.ZERO,
                "Major damage",
                AssetTaxWriteOff.WriteOffStatus.PENDING_APPROVAL,
                null,
                null,
                java.time.Instant.now()
        );

        when(repository.findById(writeOffId)).thenReturn(Optional.of(writeOff));
        when(repository.save(any(AssetTaxWriteOff.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        AssetTaxWriteOff approved = useCase.approveWriteOff(orgId, writeOffId, approverId);

        assertThat(approved.getStatus()).isEqualTo(AssetTaxWriteOff.WriteOffStatus.APPROVED);
        assertThat(approved.getApprovedBy()).isEqualTo(approverId);
        assertThat(asset.getStatus()).isEqualTo(AssetStatus.DISPOSED);
        verify(assetRepository).save(asset);
    }
}
