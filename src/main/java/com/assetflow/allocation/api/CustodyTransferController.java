package com.assetflow.allocation.api;

import com.assetflow.allocation.api.dto.AcceptCustodyTransferRequest;
import com.assetflow.allocation.api.dto.CustodyTransferResponse;
import com.assetflow.allocation.api.dto.InitiateCustodyTransferRequest;
import com.assetflow.allocation.application.CustodyTransferUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/custody-transfers")
public class CustodyTransferController {

    private final CustodyTransferUseCase useCase;

    public CustodyTransferController(CustodyTransferUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<CustodyTransferResponse> initiateTransfer(@Valid @RequestBody InitiateCustodyTransferRequest request) {
        CustodyTransferResponse response = useCase.initiateTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<CustodyTransferResponse> acceptTransfer(
            @PathVariable UUID id,
            @Valid @RequestBody AcceptCustodyTransferRequest request) {
        CustodyTransferResponse response = useCase.acceptTransfer(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CustodyTransferResponse> rejectTransfer(@PathVariable UUID id) {
        CustodyTransferResponse response = useCase.rejectTransfer(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustodyTransferResponse>> listTransfers() {
        return ResponseEntity.ok(useCase.listTransfers());
    }
}
