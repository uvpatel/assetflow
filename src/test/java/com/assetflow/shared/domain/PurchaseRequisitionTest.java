package com.assetflow.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseRequisitionTest {

    @Test
    @DisplayName("Should create purchase requisition in PENDING_APPROVAL status")
    void shouldCreateRequisition() {
        UUID orgId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId,
                requesterId,
                deptId,
                "REQ-2026-001",
                "New Developer Laptops",
                "Team expansion Q3",
                new BigDecimal("5000.00"),
                "USD",
                PurchaseRequisition.Urgency.HIGH
        );

        assertThat(req.getOrganizationId()).isEqualTo(orgId);
        assertThat(req.getRequesterId()).isEqualTo(requesterId);
        assertThat(req.getRequisitionNumber()).isEqualTo("REQ-2026-001");
        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.PENDING_APPROVAL);
        assertThat(req.getEstimatedTotalAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(req.getUrgency()).isEqualTo(PurchaseRequisition.Urgency.HIGH);
    }

    @Test
    @DisplayName("Should approve requisition and record approver details")
    void shouldApproveRequisition() {
        UUID orgId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), UUID.randomUUID(), "REQ-02", "Office Monitors", "Replacement",
                new BigDecimal("1200.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );

        req.approve(approverId, "Budget verified and approved");

        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.APPROVED);
        assertThat(req.getApproverId()).isEqualTo(approverId);
        assertThat(req.getApprovalNotes()).isEqualTo("Budget verified and approved");
        assertThat(req.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should reject requisition and record notes")
    void shouldRejectRequisition() {
        UUID orgId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-03", "Ergonomic Desks", "Q4 upgrade",
                new BigDecimal("8000.00"), "USD", PurchaseRequisition.Urgency.LOW
        );

        req.reject(approverId, "Exceeds remaining department budget");

        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.REJECTED);
        assertThat(req.getApproverId()).isEqualTo(approverId);
    }

    @Test
    @DisplayName("Should mark requisition as converted to PO")
    void shouldMarkConvertedToPo() {
        UUID orgId = UUID.randomUUID();
        UUID poId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-04", "Keyboards", "Peripherals",
                new BigDecimal("400.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );
        req.approve(UUID.randomUUID(), "Approved");
        req.markConvertedToPo(poId);

        assertThat(req.getStatus()).isEqualTo(PurchaseRequisition.RequisitionStatus.CONVERTED_TO_PO);
        assertThat(req.getConvertedPurchaseOrderId()).isEqualTo(poId);
    }

    @Test
    @DisplayName("Should reject converting unapproved requisition to PO")
    void shouldFailConvertingUnapprovedRequisition() {
        PurchaseRequisition req = PurchaseRequisition.create(
                UUID.randomUUID(), UUID.randomUUID(), null, "REQ-05", "Servers", "Cloud edge",
                new BigDecimal("15000.00"), "USD", PurchaseRequisition.Urgency.CRITICAL
        );

        assertThatThrownBy(() -> req.markConvertedToPo(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }
}
