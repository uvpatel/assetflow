package com.assetflow.shared.application;

import com.assetflow.shared.domain.*;
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
class PurchaseRequisitionUseCaseTest {

    @Mock
    private PurchaseRequisitionRepository requisitionRepository;

    @Mock
    private PurchaseRequisitionItemRepository itemRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    private PurchaseRequisitionUseCase useCase;

    private final UUID orgId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();
    private final UUID approverId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new PurchaseRequisitionUseCase(
                requisitionRepository,
                itemRepository,
                purchaseOrderRepository,
                purchaseOrderItemRepository
        );
    }

    @Test
    @DisplayName("Should create requisition with calculated total from line items")
    void shouldCreateRequisitionWithItems() {
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.save(any(PurchaseRequisitionItem.class))).thenAnswer(inv -> inv.getArgument(0));

        List<PurchaseRequisitionUseCase.RequisitionItemInput> items = List.of(
                new PurchaseRequisitionUseCase.RequisitionItemInput("Laptop", null, new BigDecimal("1200.00"), 2),
                new PurchaseRequisitionUseCase.RequisitionItemInput("Mouse", null, new BigDecimal("50.00"), 2)
        );

        PurchaseRequisition req = useCase.createRequisition(
                orgId,
                requesterId,
                null,
                "REQ-1001",
                "Workstation Upgrade",
                "New hires",
                "USD",
                PurchaseRequisition.Urgency.HIGH,
                items
        );

        assertThat(req).isNotNull();
        assertThat(req.getRequisitionNumber()).isEqualTo("REQ-1001");
        assertThat(req.getEstimatedTotalAmount()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.PENDING_APPROVAL);

        verify(itemRepository, times(2)).save(any(PurchaseRequisitionItem.class));
    }

    @Test
    @DisplayName("Should approve requisition successfully")
    void shouldApproveRequisition() {
        UUID reqId = UUID.randomUUID();
        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1002", "Office Supplies", "Stationery",
                new BigDecimal("300.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );

        when(requisitionRepository.findByOrganizationIdAndId(orgId, reqId)).thenReturn(Optional.of(req));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequisition approved = useCase.approveRequisition(orgId, reqId, approverId, "Looks good");

        assertThat(approved.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.APPROVED);
        assertThat(approved.getApproverId()).isEqualTo(approverId);
    }

    @Test
    @DisplayName("Should reject requisition with notes")
    void shouldRejectRequisition() {
        UUID reqId = UUID.randomUUID();
        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1003", "Expensive Chairs", "Comfort",
                new BigDecimal("9000.00"), "USD", PurchaseRequisition.Urgency.LOW
        );

        when(requisitionRepository.findByOrganizationIdAndId(orgId, reqId)).thenReturn(Optional.of(req));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequisition rejected = useCase.rejectRequisition(orgId, reqId, approverId, "Out of budget");

        assertThat(rejected.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.REJECTED);
        assertThat(rejected.getApprovalNotes()).isEqualTo("Out of budget");
    }

    @Test
    @DisplayName("Should convert APPROVED requisition to Purchase Order")
    void shouldConvertToPurchaseOrder() {
        UUID reqId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1004", "Monitors", "Display upgrade",
                new BigDecimal("600.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );
        req.approve(approverId, "Approved");

        PurchaseRequisitionItem reqItem = new PurchaseRequisitionItem(
                orgId, req.getId(), "Dell Monitor", null, new BigDecimal("300.00"), 2
        );

        when(requisitionRepository.findByOrganizationIdAndId(orgId, reqId)).thenReturn(Optional.of(req));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.findByRequisitionId(reqId)).thenReturn(List.of(reqItem));
        when(purchaseOrderItemRepository.save(any(PurchaseOrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseOrder po = useCase.convertToPurchaseOrder(orgId, reqId, vendorId, "PO-2026-999");

        assertThat(po).isNotNull();
        assertThat(po.getPoNumber()).isEqualTo("PO-2026-999");
        assertThat(po.getTotalAmount()).isEqualByComparingTo(new BigDecimal("600.00"));
        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.CONVERTED_TO_PO);

        verify(purchaseOrderItemRepository).save(any(PurchaseOrderItem.class));
    }

    @Test
    @DisplayName("Should throw error when attempting to convert unapproved requisition")
    void shouldFailConvertingUnapprovedRequisition() {
        UUID reqId = UUID.randomUUID();
        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1005", "Servers", "Compute",
                new BigDecimal("12000.00"), "USD", PurchaseRequisition.Urgency.CRITICAL
        );

        when(requisitionRepository.findByOrganizationIdAndId(orgId, reqId)).thenReturn(Optional.of(req));

        assertThatThrownBy(() -> useCase.convertToPurchaseOrder(orgId, reqId, UUID.randomUUID(), "PO-FAIL"))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Only APPROVED requisitions");
    }

    @Test
    @DisplayName("Should cancel requisition")
    void shouldCancelRequisition() {
        UUID reqId = UUID.randomUUID();
        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1006", "Cables", "Wiring",
                new BigDecimal("100.00"), "USD", PurchaseRequisition.Urgency.LOW
        );

        when(requisitionRepository.findByOrganizationIdAndId(orgId, reqId)).thenReturn(Optional.of(req));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequisition cancelled = useCase.cancelRequisition(orgId, reqId);

        assertThat(cancelled.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.CANCELLED);
    }
}
