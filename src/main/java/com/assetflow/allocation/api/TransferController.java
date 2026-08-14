package com.assetflow.allocation.api;

import com.assetflow.allocation.api.dto.CreateTransferRequest;
import com.assetflow.allocation.api.dto.RejectTransferRequest;
import com.assetflow.allocation.application.TransferUseCase;
import com.assetflow.allocation.domain.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping
    public ResponseEntity<TransferRequest> requestTransfer(@Valid @RequestBody CreateTransferRequest request) {
        TransferRequest transferRequest = transferUseCase.requestTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferRequest);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TransferRequest> approveTransfer(
            @PathVariable UUID id,
            Authentication authentication) {
        String reviewer = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : "system";
        TransferRequest approved = transferUseCase.approveTransfer(id, reviewer);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TransferRequest> rejectTransfer(
            @PathVariable UUID id,
            @RequestBody(required = false) RejectTransferRequest request,
            Authentication authentication) {
        String reviewer = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : "system";
        String rejectionReason = (request != null) ? request.getRejectionReason() : null;
        TransferRequest rejected = transferUseCase.rejectTransfer(id, reviewer, rejectionReason);
        return ResponseEntity.ok(rejected);
    }

    @GetMapping
    public ResponseEntity<List<TransferRequest>> listTransferRequests() {
        return ResponseEntity.ok(transferUseCase.listTransferRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferRequest> getTransferRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(transferUseCase.getTransferRequest(id));
    }
}
