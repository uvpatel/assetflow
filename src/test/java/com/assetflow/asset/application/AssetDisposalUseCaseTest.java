package com.assetflow.asset.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetDisposalRecord;
import com.assetflow.asset.domain.AssetDisposalRecordRepository;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.error.DomainViolationException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetDisposalUseCaseTest {

    @Mock
    private AssetDisposalRecordRepository disposalRepository;

    @Mock
    private AssetRepository assetRepository;

    private AssetDisposalUseCase useCase;

    private final UUID orgId = UUID.randomUUID();
    private final UUID assetId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetDisposalUseCase(disposalRepository, assetRepository);
    }

    @Test
    @DisplayName("Should initiate disposal for valid asset")
    void shouldInitiateDisposal() {
        Asset asset = new Asset(orgId, UUID.randomUUID(), "TAG-101", "MacBook Pro", "Dev laptop", "SN101", "Floor 2");
        asset.setId(assetId);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(disposalRepository.save(any(AssetDisposalRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetDisposalRecord result = useCase.initiateDisposal(
                orgId,
                assetId,
                AssetDisposalRecord.DisposalType.SALE,
                "Upgraded to M3",
                new BigDecimal("600.00"),
                "USD",
                userId,
                "CERT-01"
        );

        assertThat(result).isNotNull();
        assertThat(result.getOrganizationId()).isEqualTo(orgId);
        assertThat(result.getAssetId()).isEqualTo(assetId);
        assertThat(result.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.DRAFT);
        assertThat(result.getProceedsAmount()).isEqualByComparingTo(new BigDecimal("600.00"));
        verify(disposalRepository).save(any(AssetDisposalRecord.class));
    }

    @Test
    @DisplayName("Should throw when initiating disposal for already disposed asset")
    void shouldThrowWhenAssetAlreadyDisposed() {
        Asset asset = new Asset(orgId, UUID.randomUUID(), "TAG-101", "MacBook Pro", "Dev laptop", "SN101", "Floor 2");
        asset.setId(assetId);
        asset.setStatus(AssetStatus.DISPOSED);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> useCase.initiateDisposal(
                orgId, assetId, AssetDisposalRecord.DisposalType.SCRAP, "Damaged", BigDecimal.ZERO, "USD", userId, null
        )).isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("already disposed");
    }

    @Test
    @DisplayName("Should approve draft disposal record")
    void shouldApproveDisposal() {
        UUID disposalId = UUID.randomUUID();
        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.DONATION, "Charity", BigDecimal.ZERO, "USD", null, null
        );
        record.setId(disposalId);

        when(disposalRepository.findByOrganizationIdAndId(orgId, disposalId)).thenReturn(Optional.of(record));
        when(disposalRepository.save(any(AssetDisposalRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetDisposalRecord approved = useCase.approveDisposal(orgId, disposalId, userId);

        assertThat(approved.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.APPROVED);
        assertThat(approved.getAuthorizedByUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should execute disposal and transition asset status to DISPOSED")
    void shouldExecuteDisposal() {
        UUID disposalId = UUID.randomUUID();
        Asset asset = new Asset(orgId, UUID.randomUUID(), "TAG-101", "MacBook Pro", "Dev laptop", "SN101", "Floor 2");
        asset.setId(assetId);
        asset.setStatus(AssetStatus.AVAILABLE);

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SCRAP, "End of life", BigDecimal.ZERO, "USD", userId, null
        );
        record.setId(disposalId);
        record.approve(userId);

        when(disposalRepository.findByOrganizationIdAndId(orgId, disposalId)).thenReturn(Optional.of(record));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(disposalRepository.save(any(AssetDisposalRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetDisposalRecord executed = useCase.executeDisposal(orgId, disposalId);

        assertThat(executed.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.EXECUTED);
        assertThat(asset.getStatus()).isEqualTo(AssetStatus.DISPOSED);

        verify(assetRepository).save(asset);
        verify(disposalRepository).save(record);
    }

    @Test
    @DisplayName("Should cancel disposal record")
    void shouldCancelDisposal() {
        UUID disposalId = UUID.randomUUID();
        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SALE, "Sale plan cancelled", BigDecimal.ZERO, "USD", null, null
        );
        record.setId(disposalId);

        when(disposalRepository.findByOrganizationIdAndId(orgId, disposalId)).thenReturn(Optional.of(record));
        when(disposalRepository.save(any(AssetDisposalRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetDisposalRecord cancelled = useCase.cancelDisposal(orgId, disposalId);

        assertThat(cancelled.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should list disposal records for asset and organization")
    void shouldListDisposals() {
        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.RECYCLE, "Recycle e-waste", BigDecimal.ZERO, "USD", null, null
        );

        when(disposalRepository.findByOrganizationId(orgId)).thenReturn(List.of(record));
        when(disposalRepository.findByOrganizationIdAndAssetId(orgId, assetId)).thenReturn(List.of(record));

        List<AssetDisposalRecord> orgDisposals = useCase.getDisposalsForOrganization(orgId);
        List<AssetDisposalRecord> assetDisposals = useCase.getDisposalsForAsset(orgId, assetId);

        assertThat(orgDisposals).hasSize(1);
        assertThat(assetDisposals).hasSize(1);
    }
}
