package com.assetflow.shared.api;

import com.assetflow.shared.application.PurchaseRequisitionUseCase;
import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseRequisition;
import com.assetflow.shared.domain.PurchaseRequisitionItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/purchase-requisitions")
public class PurchaseRequisitionController {

    private final PurchaseRequisitionUseCase useCase;

    public PurchaseRequisitionController(PurchaseRequisitionUseCase useCase) {
        this.useCase = useCase;
    }

    public record RequisitionItemRequest(
            String itemDescription,
            UUID categoryId,
            BigDecimal estimatedUnitPrice,
            int quantity
    ) {}

    public record CreateRequisitionRequest(
            UUID requesterId,
            UUID departmentId,
            String requisitionNumber,
            String title,
            String businessJustification,
            String currency,
            String urgency,
            List<RequisitionItemRequest> items
    ) {}

    public record ApprovalActionRequest(
            UUID approverId,
            String notes
    ) {}

    public record ConvertToPoRequest(
            UUID vendorId,
            String poNumber
    ) {}

    public record RequisitionResponse(
            UUID id,
            UUID organizationId,
            String requisitionNumber,
            UUID requesterId,
            UUID departmentId,
            String title,
            String businessJustification,
            BigDecimal estimatedTotalAmount,
            String currency,
            String urgency,
            String status,
            UUID approverId,
            String approvalNotes,
            Instant approvedAt,
            UUID convertedPurchaseOrderId,
            Instant createdAt
    ) {}

    public record RequisitionItemResponse(
            UUID id,
            UUID organizationId,
            UUID requisitionId,
            String itemDescription,
            UUID categoryId,
            BigDecimal estimatedUnitPrice,
            int quantity,
            BigDecimal totalAmount
    ) {}

    public record ConvertedPoResponse(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String poNumber,
            String status,
            BigDecimal totalAmount
    ) {}

    @PostMapping
    public ResponseEntity<RequisitionResponse> createRequisition(
            @PathVariable UUID organizationId,
            @RequestBody CreateRequisitionRequest request) {
        List<PurchaseRequisitionUseCase.RequisitionItemInput> itemInputs = request.items() != null
                ? request.items().stream()
                .map(i -> new PurchaseRequisitionUseCase.RequisitionItemInput(
                        i.itemDescription(), i.categoryId(), i.estimatedUnitPrice(), i.quantity()))
                .toList()
                : List.of();

        PurchaseRequisition.Urgency urgency = PurchaseRequisition.Urgency.NORMAL;
        if (request.urgency() != null) {
            try {
                urgency = PurchaseRequisition.Urgency.valueOf(request.urgency().toUpperCase());
            } catch (Exception ignored) {}
        }

        PurchaseRequisition req = useCase.createRequisition(
                organizationId,
                request.requesterId(),
                request.departmentId(),
                request.requisitionNumber(),
                request.title(),
                request.businessJustification(),
                request.currency(),
                urgency,
                itemInputs
        );
        return ResponseEntity.ok(toResponse(req));
    }

    @PostMapping("/{requisitionId}/approve")
    public ResponseEntity<RequisitionResponse> approveRequisition(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId,
            @RequestBody ApprovalActionRequest request) {
        PurchaseRequisition req = useCase.approveRequisition(organizationId, requisitionId, request.approverId(), request.notes());
        return ResponseEntity.ok(toResponse(req));
    }

    @PostMapping("/{requisitionId}/reject")
    public ResponseEntity<RequisitionResponse> rejectRequisition(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId,
            @RequestBody ApprovalActionRequest request) {
        PurchaseRequisition req = useCase.rejectRequisition(organizationId, requisitionId, request.approverId(), request.notes());
        return ResponseEntity.ok(toResponse(req));
    }

    @PostMapping("/{requisitionId}/convert-to-po")
    public ResponseEntity<ConvertedPoResponse> convertToPurchaseOrder(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId,
            @RequestBody ConvertToPoRequest request) {
        PurchaseOrder po = useCase.convertToPurchaseOrder(organizationId, requisitionId, request.vendorId(), request.poNumber());
        return ResponseEntity.ok(new ConvertedPoResponse(
                po.getId(),
                po.getOrganizationId(),
                po.getVendorId(),
                po.getPoNumber(),
                po.getStatus().name(),
                po.getTotalAmount()
        ));
    }

    @PostMapping("/{requisitionId}/cancel")
    public ResponseEntity<RequisitionResponse> cancelRequisition(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId) {
        PurchaseRequisition req = useCase.cancelRequisition(organizationId, requisitionId);
        return ResponseEntity.ok(toResponse(req));
    }

    @GetMapping
    public ResponseEntity<List<RequisitionResponse>> getRequisitions(@PathVariable UUID organizationId) {
        List<RequisitionResponse> list = useCase.getRequisitions(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{requisitionId}")
    public ResponseEntity<RequisitionResponse> getRequisitionById(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId) {
        return useCase.getRequisitionById(organizationId, requisitionId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{requisitionId}/items")
    public ResponseEntity<List<RequisitionItemResponse>> getRequisitionItems(
            @PathVariable UUID organizationId,
            @PathVariable UUID requisitionId) {
        List<RequisitionItemResponse> list = useCase.getRequisitionItems(organizationId, requisitionId)
                .stream()
                .map(this::toItemResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private RequisitionResponse toResponse(PurchaseRequisition r) {
        return new RequisitionResponse(
                r.getId(),
                r.getOrganizationId(),
                r.getRequisitionNumber(),
                r.getRequesterId(),
                r.getDepartmentId(),
                r.getTitle(),
                r.getBusinessJustification(),
                r.getEstimatedTotalAmount(),
                r.getCurrency(),
                r.getUrgency().name(),
                r.getStatus().name(),
                r.getApproverId(),
                r.getApprovalNotes(),
                r.getApprovedAt(),
                r.getConvertedPurchaseOrderId(),
                r.getCreatedAt()
        );
    }

    private RequisitionItemResponse toItemResponse(PurchaseRequisitionItem item) {
        return new RequisitionItemResponse(
                item.getId(),
                item.getOrganizationId(),
                item.getRequisitionId(),
                item.getItemDescription(),
                item.getCategoryId(),
                item.getEstimatedUnitPrice(),
                item.getQuantity(),
                item.calculateTotal()
        );
    }
}
