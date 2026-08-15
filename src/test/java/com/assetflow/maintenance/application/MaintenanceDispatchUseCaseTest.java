package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicyRepository;
import com.assetflow.maintenance.domain.TechnicianWorkload;
import com.assetflow.maintenance.domain.TechnicianWorkloadRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceDispatchUseCaseTest {

    @Mock
    private MaintenanceRequestRepository requestRepository;

    @Mock
    private TechnicianWorkloadRepository workloadRepository;

    @Mock
    private MaintenanceSlaPolicyRepository slaPolicyRepository;

    private MaintenanceDispatchUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new MaintenanceDispatchUseCase(requestRepository, workloadRepository, slaPolicyRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should successfully dispatch work order to target technician and set SLA deadline")
    void shouldDispatchWorkOrderToTargetTechnician() {
        UUID requestId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        MaintenanceRequest request = new MaintenanceRequest(orgId, UUID.randomUUID(), UUID.randomUUID(), "HVAC Repair", "Fix AC", "HIGH");
        request.setId(requestId);

        TechnicianWorkload workload = TechnicianWorkload.register(orgId, technicianId, 5, "HVAC");

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(workloadRepository.findByOrganizationIdAndTechnicianEmployeeId(orgId, technicianId)).thenReturn(Optional.of(workload));
        when(requestRepository.save(any(MaintenanceRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceRequest dispatched = useCase.dispatchWorkOrder(requestId, technicianId);

        assertThat(dispatched.getAssignedTechnicianId()).isEqualTo(technicianId);
        assertThat(dispatched.getStatus()).isEqualTo("APPROVED");
        assertThat(dispatched.getTargetResolutionAt()).isNotNull();
        assertThat(dispatched.getSlaStatus()).isEqualTo("IN_SLA");
        assertThat(workload.getActiveTaskCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should auto-balance dispatch to available technician with lowest active task count")
    void shouldAutoBalanceDispatch() {
        UUID requestId = UUID.randomUUID();
        UUID tech1 = UUID.randomUUID();
        UUID tech2 = UUID.randomUUID();

        MaintenanceRequest request = new MaintenanceRequest(orgId, UUID.randomUUID(), UUID.randomUUID(), "Electrical Fix", "Fuse blown", "CRITICAL");
        request.setId(requestId);

        TechnicianWorkload busyTech = new TechnicianWorkload(UUID.randomUUID(), orgId, tech1, 3, 5, "Electrical", Instant.now(), Instant.now());
        TechnicianWorkload freeTech = new TechnicianWorkload(UUID.randomUUID(), orgId, tech2, 1, 5, "Electrical", Instant.now(), Instant.now());

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(workloadRepository.findByOrganizationId(orgId)).thenReturn(List.of(busyTech, freeTech));
        when(workloadRepository.findByOrganizationIdAndTechnicianEmployeeId(orgId, tech2)).thenReturn(Optional.of(freeTech));
        when(requestRepository.save(any(MaintenanceRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceRequest dispatched = useCase.dispatchWorkOrder(requestId, null);

        assertThat(dispatched.getAssignedTechnicianId()).isEqualTo(tech2);
        assertThat(freeTech.getActiveTaskCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should evaluate and mark overdue maintenance requests as SLA_VIOLATED")
    void shouldEvaluateSlaBreaches() {
        MaintenanceRequest request = new MaintenanceRequest(orgId, UUID.randomUUID(), UUID.randomUUID(), "Network Outage", "Switch down", "CRITICAL");
        request.setId(UUID.randomUUID());
        request.setStatus("ASSIGNED");
        request.setTargetResolutionAt(Instant.now().minus(2, ChronoUnit.HOURS));

        when(requestRepository.findByOrganizationId(orgId)).thenReturn(List.of(request));

        int breachedCount = useCase.evaluateSlaStatuses();

        assertThat(breachedCount).isEqualTo(1);
        assertThat(request.getSlaStatus()).isEqualTo("VIOLATED");
        verify(requestRepository).save(request);
    }
}
