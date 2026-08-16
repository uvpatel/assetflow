package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetDisposalRecordTest {

    @Test
    @DisplayName("Should correctly initiate disposal record in DRAFT status")
    void shouldInitiateDisposalRecord() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId,
                assetId,
                AssetDisposalRecord.DisposalType.SALE,
                "Obsolescence after 5-year cycle",
                new BigDecimal("500.00"),
                "USD",
                userId,
                "CERT-2026-001"
        );

        assertThat(record.getOrganizationId()).isEqualTo(orgId);
        assertThat(record.getAssetId()).isEqualTo(assetId);
        assertThat(record.getDisposalType()).isEqualTo(AssetDisposalRecord.DisposalType.SALE);
        assertThat(record.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.DRAFT);
        assertThat(record.getProceedsAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(record.getCertificateReference()).isEqualTo("CERT-2026-001");
    }

    @Test
    @DisplayName("Should approve DRAFT disposal record")
    void shouldApproveDraftRecord() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SCRAP, "Damaged beyond repair",
                BigDecimal.ZERO, "USD", null, null
        );

        record.approve(userId);

        assertThat(record.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.APPROVED);
        assertThat(record.getAuthorizedByUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should execute APPROVED disposal record")
    void shouldExecuteApprovedRecord() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.DONATION, "Charity donation",
                BigDecimal.ZERO, "USD", userId, "DON-883"
        );
        record.approve(userId);
        record.execute();

        assertThat(record.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.EXECUTED);
        assertThat(record.getDisposalDate()).isNotNull();
    }

    @Test
    @DisplayName("Should fail approving non-draft record")
    void shouldFailApprovingNonDraftRecord() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.RECYCLE, "E-waste recycling",
                BigDecimal.ZERO, "USD", null, null
        );
        record.execute();

        assertThatThrownBy(() -> record.approve(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should cancel DRAFT or APPROVED record and reject cancelling EXECUTED record")
    void shouldHandleCancellation() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.LOST_WRITE_OFF, "Stolen during transit",
                BigDecimal.ZERO, "USD", null, null
        );
        record.cancel();
        assertThat(record.getStatus()).isEqualTo(AssetDisposalRecord.DisposalStatus.CANCELLED);

        AssetDisposalRecord executedRecord = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SALE, "Sold at auction",
                new BigDecimal("250.00"), "USD", null, null
        );
        executedRecord.execute();

        assertThatThrownBy(executedRecord::cancel)
                .isInstanceOf(IllegalStateException.class);
    }
}
