package com.assetflow.shared.application;

import com.assetflow.shared.domain.VendorContract;
import com.assetflow.shared.domain.VendorContractRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorContractUseCaseTest {

    @Mock
    private VendorContractRepository repository;

    private VendorContractUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new VendorContractUseCase(repository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should query contracts requiring renewal notice correctly")
    void shouldGetContractsRequiringRenewalNotice() {
        UUID vendorId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        VendorContract expiring = VendorContract.create(
                orgId, vendorId, "CNT-EXP", "HARDWARE", today.minusMonths(11), today.plusDays(15), new BigDecimal("10000.00"), false, 30
        );

        when(repository.findByOrganizationId(orgId)).thenReturn(List.of(expiring));

        List<VendorContract> result = useCase.getContractsRequiringRenewalNotice(orgId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContractNumber()).isEqualTo("CNT-EXP");
    }

    @Test
    @DisplayName("Should terminate vendor contract successfully")
    void shouldTerminateContract() {
        UUID contractId = UUID.randomUUID();
        VendorContract contract = VendorContract.create(
                orgId, UUID.randomUUID(), "CNT-TERM", "SERVICES", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5), new BigDecimal("5000.00"), false
        );

        when(repository.findById(contractId)).thenReturn(Optional.of(contract));
        when(repository.save(any(VendorContract.class))).thenAnswer(inv -> inv.getArgument(0));

        VendorContract terminated = useCase.terminateContract(orgId, contractId);

        assertThat(terminated.getStatus()).isEqualTo(VendorContract.ContractStatus.TERMINATED);
        verify(repository).save(contract);
    }
}
