package com.assetflow.maintenance.application;

import com.assetflow.maintenance.domain.MaintenancePartItem;
import com.assetflow.maintenance.domain.MaintenancePartItemRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenancePartsUseCaseTest {

    @Mock
    private MaintenancePartItemRepository repository;

    private MaintenancePartsUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new MaintenancePartsUseCase(repository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should add part with sparePartId and mark inventory deducted")
    void shouldAddPartAndDeductInventory() {
        UUID maintId = UUID.randomUUID();
        UUID sparePartId = UUID.randomUUID();

        when(repository.save(any(MaintenancePartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenancePartItem item = useCase.addPartToMaintenance(
                orgId,
                maintId,
                sparePartId,
                "NVMe SSD 1TB",
                "SSD-1TB-NVME",
                2,
                new BigDecimal("120.00")
        );

        assertThat(item.getSparePartId()).isEqualTo(sparePartId);
        assertThat(item.isInventoryDeducted()).isTrue();
        assertThat(item.getTotalCost()).isEqualByComparingTo(new BigDecimal("240.00"));
        verify(repository).save(any(MaintenancePartItem.class));
    }

    @Test
    @DisplayName("Should calculate total parts cost for a maintenance work order")
    void shouldCalculateTotalPartsCost() {
        UUID maintId = UUID.randomUUID();

        MaintenancePartItem item1 = MaintenancePartItem.create(orgId, maintId, "Oil Filter", "OF-1", 1, new BigDecimal("25.00"));
        MaintenancePartItem item2 = MaintenancePartItem.create(orgId, maintId, "Synthetic Engine Oil", "SEO-5L", 2, new BigDecimal("45.00"));

        when(repository.findByOrganizationIdAndMaintenanceId(orgId, maintId)).thenReturn(List.of(item1, item2));

        BigDecimal totalPartsCost = useCase.calculateTotalPartsCost(orgId, maintId);

        assertThat(totalPartsCost).isEqualByComparingTo(new BigDecimal("115.00"));
    }
}
