package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.AssetMaintenanceSchedule;
import com.assetflow.maintenance.domain.AssetMaintenanceScheduleRepository;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetMaintenanceScheduleUseCaseTest {

    @Mock
    private AssetMaintenanceScheduleRepository repository;

    @Mock
    private MaintenanceRequestRepository maintenanceRequestRepository;

    private AssetMaintenanceScheduleUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetMaintenanceScheduleUseCase(repository, maintenanceRequestRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should auto-generate PM maintenance request when schedule is due")
    void shouldGenerateDueWorkOrders() {
        UUID assetId = UUID.randomUUID();
        Instant duePast = Instant.now().minus(1, ChronoUnit.DAYS);
        AssetMaintenanceSchedule schedule = AssetMaintenanceSchedule.create(
                orgId, assetId, "Air Compressor Calibration", 30, duePast, true, "HIGH"
        );

        when(repository.findByOrganizationId(orgId)).thenReturn(List.of(schedule));
        when(maintenanceRequestRepository.save(any(MaintenanceRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repository.save(any(AssetMaintenanceSchedule.class))).thenAnswer(inv -> inv.getArgument(0));

        List<MaintenanceRequest> generated = useCase.generateDueWorkOrders(orgId);

        assertThat(generated).hasSize(1);
        assertThat(generated.get(0).getTitle()).isEqualTo("Air Compressor Calibration");
        assertThat(generated.get(0).getPriority()).isEqualTo("HIGH");

        verify(maintenanceRequestRepository).save(any(MaintenanceRequest.class));
        verify(repository).save(schedule);
    }
}
