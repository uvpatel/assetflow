package com.assetflow.shared.api;

import com.assetflow.shared.application.PurchaseRequisitionUseCase;
import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseRequisition;
import com.assetflow.shared.domain.PurchaseRequisitionItem;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PurchaseRequisitionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PurchaseRequisitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseRequisitionUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create purchase requisition via POST")
    void shouldCreateRequisition() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, requesterId, null, "REQ-1001", "MacBook Pros", "Team expansion",
                new BigDecimal("4000.00"), "USD", PurchaseRequisition.Urgency.HIGH
        );

        when(useCase.createRequisition(
                eq(orgId), eq(requesterId), eq(null), eq("REQ-1001"), eq("MacBook Pros"),
                eq("Team expansion"), eq("USD"), eq(PurchaseRequisition.Urgency.HIGH), any()
        )).thenReturn(req);

        PurchaseRequisitionController.CreateRequisitionRequest request = new PurchaseRequisitionController.CreateRequisitionRequest(
                requesterId, null, "REQ-1001", "MacBook Pros", "Team expansion", "USD", "HIGH", List.of()
        );

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/purchase-requisitions", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requisitionNumber").value("REQ-1001"))
                .andExpect(jsonPath("$.estimatedTotalAmount").value(4000.00))
                .andExpect(jsonPath("$.urgency").value("HIGH"))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
    }

    @Test
    @DisplayName("Should approve purchase requisition via POST")
    void shouldApproveRequisition() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-1002", "Office Desks", "Furniture",
                new BigDecimal("1500.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );
        req.approve(approverId, "Approved by manager");

        when(useCase.approveRequisition(orgId, reqId, approverId, "Approved by manager")).thenReturn(req);

        PurchaseRequisitionController.ApprovalActionRequest request =
                new PurchaseRequisitionController.ApprovalActionRequest(approverId, "Approved by manager");

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}/approve", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approvalNotes").value("Approved by manager"));
    }

    @Test
    @DisplayName("Should reject purchase requisition via POST")
    void shouldRejectRequisition() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-1003", "Expensive Monitors", "Displays",
                new BigDecimal("5000.00"), "USD", PurchaseRequisition.Urgency.LOW
        );
        req.reject(approverId, "Over budget");

        when(useCase.rejectRequisition(orgId, reqId, approverId, "Over budget")).thenReturn(req);

        PurchaseRequisitionController.ApprovalActionRequest request =
                new PurchaseRequisitionController.ApprovalActionRequest(approverId, "Over budget");

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}/reject", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("Should convert approved requisition to Purchase Order via POST")
    void shouldConvertToPurchaseOrder() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        PurchaseOrder po = PurchaseOrder.create(orgId, vendorId, "PO-2026-555", new BigDecimal("3000.00"));

        when(useCase.convertToPurchaseOrder(orgId, reqId, vendorId, "PO-2026-555")).thenReturn(po);

        PurchaseRequisitionController.ConvertToPoRequest request =
                new PurchaseRequisitionController.ConvertToPoRequest(vendorId, "PO-2026-555");

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}/convert-to-po", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poNumber").value("PO-2026-555"))
                .andExpect(jsonPath("$.totalAmount").value(3000.00));
    }

    @Test
    @DisplayName("Should cancel purchase requisition via POST")
    void shouldCancelRequisition() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-1004", "Keyboards", "Hardware",
                new BigDecimal("200.00"), "USD", PurchaseRequisition.Urgency.LOW
        );
        req.cancel();

        when(useCase.cancelRequisition(orgId, reqId)).thenReturn(req);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}/cancel", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Should get all requisitions and line items")
    void shouldGetRequisitionsAndItems() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();

        PurchaseRequisition req = PurchaseRequisition.create(
                orgId, UUID.randomUUID(), null, "REQ-1005", "Chairs", "Office",
                new BigDecimal("400.00"), "USD", PurchaseRequisition.Urgency.NORMAL
        );

        PurchaseRequisitionItem item = new PurchaseRequisitionItem(
                orgId, reqId, "Ergonomic Chair", null, new BigDecimal("200.00"), 2
        );

        when(useCase.getRequisitions(orgId)).thenReturn(List.of(req));
        when(useCase.getRequisitionById(orgId, reqId)).thenReturn(Optional.of(req));
        when(useCase.getRequisitionItems(orgId, reqId)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/purchase-requisitions", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requisitionNumber").value("REQ-1005"));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requisitionNumber").value("REQ-1005"));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/purchase-requisitions/{requisitionId}/items", orgId, reqId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemDescription").value("Ergonomic Chair"))
                .andExpect(jsonPath("$[0].totalAmount").value(400.00));
    }
}
