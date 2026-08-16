package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRunRepository;
import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetDepreciationUseCaseTest {

    @Mock
    private AssetDepreciationScheduleRepository repository;

    @Mock
    private AssetDepreciationPeriodClosingRunRepository periodClosingRunRepository;

    private AssetDepreciationUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetDepreciationUseCase(repository, periodClosingRunRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should execute period closing run for organization asset schedules successfully")
    void shouldRunPeriodClosing() {
        UUID assetId = UUID.randomUUID();
        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                orgId, assetId, AssetDepreciationSchedule.DepreciationMethod.STRAIGHT_LINE, new BigDecimal("1200.00"), new BigDecimal("200.00"), 10
        );

        when(periodClosingRunRepository.findByOrganizationIdAndPeriodYearAndPeriodMonth(orgId, 2026, 8)).thenReturn(Optional.empty());
        when(repository.findByOrganizationId(orgId)).thenReturn(List.of(schedule));
        when(repository.save(any(AssetDepreciationSchedule.class))).thenAnswer(inv -> inv.getArgument(0));
        when(periodClosingRunRepository.save(any(AssetDepreciationPeriodClosingRun.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetDepreciationPeriodClosingRun run = useCase.runPeriodClosing(orgId, 2026, 8, 5);

        assertThat(run.getPeriodYear()).isEqualTo(2026);
        assertThat(run.getPeriodMonth()).isEqualTo(8);
        assertThat(run.getProcessedSchedulesCount()).isEqualTo(1);
        assertThat(run.getTotalDepreciationAmount()).isEqualByComparingTo(new BigDecimal("500.00"));

        verify(periodClosingRunRepository).save(any(AssetDepreciationPeriodClosingRun.class));
    }
}
