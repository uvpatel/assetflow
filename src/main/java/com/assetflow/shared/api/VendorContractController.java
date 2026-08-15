package com.assetflow.shared.api;

import com.assetflow.shared.application.VendorContractUseCase;
import com.assetflow.shared.domain.VendorContract;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/vendor-contracts")
public class VendorContractController {

    private final VendorContractUseCase useCase;

    public VendorContractController(VendorContractUseCase useCase) {
        this.useCase = useCase;
    }

    public record CreateContractRequest(
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew
    ) {}

    public record RenewContractRequest(
            LocalDate newEndDate,
            BigDecimal newContractValue
    ) {}

    public record ContractResponse(
            UUID id,
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew,
            boolean isExpired
    ) {}

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(
            @PathVariable UUID organizationId,
            @RequestBody CreateContractRequest request) {
        VendorContract contract = useCase.createContract(
                organizationId,
                request.vendorId(),
                request.contractNumber(),
                request.contractType(),
                request.startDate(),
                request.endDate(),
                request.contractValue(),
                request.isAutoRenew()
        );
        return ResponseEntity.ok(toResponse(contract));
    }

    @PostMapping("/{contractId}/renew")
    public ResponseEntity<ContractResponse> renewContract(
            @PathVariable UUID contractId,
            @RequestBody RenewContractRequest request) {
        VendorContract contract = useCase.renewContract(contractId, request.newEndDate(), request.newContractValue());
        return ResponseEntity.ok(toResponse(contract));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<ContractResponse>> getExpiringContracts(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "30") int daysThreshold) {
        List<ContractResponse> list = useCase.getExpiringContracts(organizationId, daysThreshold)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private ContractResponse toResponse(VendorContract c) {
        return new ContractResponse(
                c.getId(),
                c.getOrganizationId(),
                c.getVendorId(),
                c.getContractNumber(),
                c.getContractType(),
                c.getStartDate(),
                c.getEndDate(),
                c.getContractValue(),
                c.isAutoRenew(),
                c.isExpired(LocalDate.now())
        );
    }
}
