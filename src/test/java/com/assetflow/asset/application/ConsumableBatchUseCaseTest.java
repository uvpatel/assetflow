package com.assetflow.asset.application;

import com.assetflow.asset.domain.ConsumableAsset;
import com.assetflow.asset.domain.ConsumableAssetRepository;
import com.assetflow.asset.domain.ConsumableBatch;
import com.assetflow.asset.domain.ConsumableBatchRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsumableBatchUseCaseTest {

    @Mock
    private ConsumableBatchRepository repository;

    @Mock
    private ConsumableAssetRepository consumableAssetRepository;

    private ConsumableBatchUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new ConsumableBatchUseCase(repository, consumableAssetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should query expiring batches correctly")
    void shouldGetExpiringBatches() {
        UUID assetId = UUID.randomUUID();
        Instant expirySoon = Instant.now().plus(10, ChronoUnit.DAYS);
        ConsumableBatch batch = ConsumableBatch.create(orgId, assetId, "BATCH-EXP", 50, expirySoon);

        when(repository.findByOrganizationId(orgId)).thenReturn(List.of(batch));

        List<ConsumableBatch> expiring = useCase.getExpiringBatches(orgId, 30);

        assertThat(expiring).hasSize(1);
        assertThat(expiring.get(0).getBatchNumber()).isEqualTo("BATCH-EXP");
    }

    @Test
    @DisplayName("Should generate reorder alerts for low stock assets")
    void shouldGenerateStockReorderAlerts() {
        UUID assetId = UUID.randomUUID();
        ConsumableAsset lowStockAsset = ConsumableAsset.create(
                orgId, "Printer Paper", "SKU-PAPER-01", UUID.randomUUID(), 5, 20, "Ream", new BigDecimal("5.50")
        );

        when(consumableAssetRepository.findByOrganizationId(orgId)).thenReturn(List.of(lowStockAsset));
        when(repository.findByOrganizationId(orgId)).thenReturn(List.of());

        List<ConsumableBatchUseCase.ReorderAlertSummary> alerts = useCase.generateStockReorderAlerts(orgId);

        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).assetName()).isEqualTo("Printer Paper");
        assertThat(alerts.get(0).recommendedReorderQuantity()).isEqualTo(35);
    }
}
