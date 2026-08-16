package com.assetflow.financial.application;

import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.asset.domain.AssetDepreciationScheduleRepository;
import com.assetflow.financial.domain.DepreciationForecastProjection;
import com.assetflow.financial.domain.DepreciationForecastProjectionRepository;
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
class AssetDepreciationForecastUseCaseTest {

    @Mock
    private DepreciationForecastProjectionRepository repository;

    @Mock
    private AssetDepreciationScheduleRepository scheduleRepository;

    private AssetDepreciationForecastUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetDepreciationForecastUseCase(repository, scheduleRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should generate financial depreciation forecast projection for target fiscal year")
    void shouldGenerateForecast() {
        UUID assetId = UUID.randomUUID();
        AssetDepreciationSchedule schedule = AssetDepreciationSchedule.create(
                orgId, assetId, AssetDepreciationSchedule.DepreciationMethod.STRAIGHT_LINE, new BigDecimal("12000.00"), new BigDecimal("2000.00"), 60
        );

        when(scheduleRepository.findByOrganizationId(orgId)).thenReturn(List.of(schedule));
        when(repository.findByOrganizationIdAndFiscalYear(orgId, 2027)).thenReturn(Optional.empty());
        when(repository.save(any(DepreciationForecastProjection.class))).thenAnswer(inv -> inv.getArgument(0));

        DepreciationForecastProjection result = useCase.generateForecast(orgId, 2027);

        assertThat(result.getFiscalYear()).isEqualTo(2027);
        assertThat(result.getProjectedDepreciation()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(result.getProjectedEndingBookValue()).isEqualByComparingTo(new BigDecimal("10000.00"));

        verify(repository).save(any(DepreciationForecastProjection.class));
    }
}
