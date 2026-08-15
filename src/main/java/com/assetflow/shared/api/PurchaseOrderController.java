package com.assetflow.shared.api;

import com.assetflow.asset.domain.Asset;
import com.assetflow.shared.application.PurchaseOrderUseCase;
import com.assetflow.shared.domain.PurchaseOrder;
import com.assetflow.shared.domain.PurchaseOrderItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public record AddPoItemRequest(
            String itemDescription,
            UUID categoryId,
            BigDecimal unitPrice,
            int quantityOrdered
    ) {}

    public record ReceivePoItemRequest(
            int quantityReceived
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

    @PostMapping("/{poId}/items")
    public ResponseEntity<PurchaseOrderItem> addLineItem(
            @PathVariable UUID organizationId,
            @PathVariable UUID poId,
            @RequestBody AddPoItemRequest request) {
        PurchaseOrderItem item = useCase.addPurchaseOrderItem(
                organizationId,
                poId,
                request.itemDescription(),
                request.categoryId(),
                request.unitPrice(),
                request.quantityOrdered()
        );
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{poId}/items/{itemId}/receive")
    public ResponseEntity<List<Asset>> receiveItem(
            @PathVariable UUID organizationId,
            @PathVariable UUID poId,
            @PathVariable UUID itemId,
            @RequestBody ReceivePoItemRequest request) {
        List<Asset> createdAssets = useCase.receivePurchaseOrderItem(
                organizationId,
                poId,
                itemId,
                request.quantityReceived()
        );
        return ResponseEntity.ok(createdAssets);
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
