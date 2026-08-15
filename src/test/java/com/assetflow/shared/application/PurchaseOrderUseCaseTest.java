package com.assetflow.shared.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseOrderItem;
import com.assetflow.shared.domain.PurchaseOrderItemRepository;
import com.assetflow.shared.domain.PurchaseOrderRepository;
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
class PurchaseOrderUseCaseTest {

    @Mock
    private PurchaseOrderRepository repository;

    @Mock
    private PurchaseOrderItemRepository itemRepository;

    @Mock
    private AssetRepository assetRepository;

    private PurchaseOrderUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new PurchaseOrderUseCase(repository, itemRepository, assetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should receive PO item, create IN_STOCK assets, and fulfill PO when fully received")
    void shouldReceivePoItemAndAutoCreateAssets() {
        UUID poId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        PurchaseOrder po = PurchaseOrder.create(orgId, vendorId, "PO-2026-001", new BigDecimal("3000.00"));
        po.issue();

        PurchaseOrderItem item = new PurchaseOrderItem(orgId, poId, "Ergonomic Chairs", null, new BigDecimal("300.00"), 2);

        when(repository.findById(poId)).thenReturn(Optional.of(po));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(PurchaseOrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.findByPurchaseOrderId(poId)).thenReturn(List.of(item));
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Asset> createdAssets = useCase.receivePurchaseOrderItem(orgId, poId, itemId, 2);

        assertThat(createdAssets).hasSize(2);
        assertThat(createdAssets.get(0).getStatus()).isEqualTo(AssetStatus.AVAILABLE);
        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.FULFILLED);
        verify(repository).save(po);
    }
}
