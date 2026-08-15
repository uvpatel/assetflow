package com.assetflow.shared.api;

import com.assetflow.shared.application.PurchaseOrderUseCase;
import com.assetflow.shared.domain.PurchaseOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderUseCase useCase;

    public PurchaseOrderController(PurchaseOrderUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreatePurchaseOrderRequest(
            UUID vendorId,
            String poNumber,
            BigDecimal initialTotal
    ) {}

    public record PurchaseOrderResponse(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String poNumber,
            String status,
            BigDecimal totalAmount,
            Instant createdAt
    ) {}

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
            @PathVariable UUID organizationId,
            @RequestBody CreatePurchaseOrderRequest request) {
        PurchaseOrder po = useCase.createPurchaseOrder(
                organizationId,
                request.vendorId(),
                request.poNumber(),
                request.initialTotal()
        );
        return ResponseEntity.ok(toResponse(po));
    }

    @PostMapping("/{poId}/issue")
    public ResponseEntity<PurchaseOrderResponse> issuePurchaseOrder(@PathVariable UUID poId) {
        PurchaseOrder po = useCase.issuePurchaseOrder(poId);
        return ResponseEntity.ok(toResponse(po));
    }

    @PostMapping("/{poId}/fulfill")
    public ResponseEntity<PurchaseOrderResponse> fulfillPurchaseOrder(@PathVariable UUID poId) {
        PurchaseOrder po = useCase.fulfillPurchaseOrder(poId);
        return ResponseEntity.ok(toResponse(po));
    }

    @PostMapping("/{poId}/cancel")
    public ResponseEntity<PurchaseOrderResponse> cancelPurchaseOrder(@PathVariable UUID poId) {
        PurchaseOrder po = useCase.cancelPurchaseOrder(poId);
        return ResponseEntity.ok(toResponse(po));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getPurchaseOrders(
            @PathVariable UUID organizationId,
            @RequestParam UUID vendorId) {
        List<PurchaseOrderResponse> list = useCase.getPurchaseOrdersForVendor(organizationId, vendorId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder po) {
        return new PurchaseOrderResponse(
                po.getId(),
                po.getOrganizationId(),
                po.getVendorId(),
                po.getPoNumber(),
                po.getStatus().name(),
                po.getTotalAmount(),
                po.getCreatedAt()
        );
    }
}
