package com.assetflow.allocation.api;

import com.assetflow.allocation.application.TemporaryAssetCheckoutUseCase;
import com.assetflow.allocation.domain.TemporaryAssetCheckout;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/temporary-checkouts")
public class TemporaryAssetCheckoutController {

    private final TemporaryAssetCheckoutUseCase useCase;

    public TemporaryAssetCheckoutController(TemporaryAssetCheckoutUseCase useCase) {
        this.useCase = useCase;
    }

    public record CheckoutRequest(
            UUID assetId,
            UUID borrowerEmployeeId,
            Instant expectedReturnTime,
            String checkoutConditionNotes
    ) {}

    public record ReturnRequest(String returnConditionNotes) {}

    public record CheckoutResponse(
            UUID id,
            UUID organizationId,
            UUID assetId,
            UUID borrowerEmployeeId,
            Instant checkoutTime,
            Instant expectedReturnTime,
            Instant actualReturnTime,
            String checkoutConditionNotes,
            String returnConditionNotes,
            String status
    ) {}

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkoutAsset(
            @PathVariable UUID organizationId,
            @RequestBody CheckoutRequest request) {
        TemporaryAssetCheckout checkout = useCase.checkoutAsset(
                organizationId,
                request.assetId(),
                request.borrowerEmployeeId(),
                request.expectedReturnTime(),
                request.checkoutConditionNotes()
        );
        return ResponseEntity.ok(toResponse(checkout));
    }

    @PostMapping("/{checkoutId}/return")
    public ResponseEntity<CheckoutResponse> returnAsset(
            @PathVariable UUID checkoutId,
            @RequestBody ReturnRequest request) {
        TemporaryAssetCheckout checkout = useCase.returnAsset(checkoutId, request.returnConditionNotes());
        return ResponseEntity.ok(toResponse(checkout));
    }

    @GetMapping("/borrowers/{borrowerEmployeeId}")
    public ResponseEntity<List<CheckoutResponse>> getBorrowerCheckouts(
            @PathVariable UUID organizationId,
            @PathVariable UUID borrowerEmployeeId) {
        List<CheckoutResponse> list = useCase.getCheckoutsForBorrower(organizationId, borrowerEmployeeId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private CheckoutResponse toResponse(TemporaryAssetCheckout c) {
        return new CheckoutResponse(
                c.getId(),
                c.getOrganizationId(),
                c.getAssetId(),
                c.getBorrowerEmployeeId(),
                c.getCheckoutTime(),
                c.getExpectedReturnTime(),
                c.getActualReturnTime(),
                c.getCheckoutConditionNotes(),
                c.getReturnConditionNotes(),
                c.getStatus().name()
        );
    }
}
