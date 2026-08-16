package com.assetflow.shared.api;

import com.assetflow.shared.application.VendorContractUseCase;
import com.assetflow.shared.domain.VendorContract;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            boolean isAutoRenew,
            int noticePeriodDays
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
            int noticePeriodDays,
            String status,
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
                request.isAutoRenew(),
                request.noticePeriodDays()
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

    @PostMapping("/{contractId}/terminate")
    public ResponseEntity<ContractResponse> terminateContract(
            @PathVariable UUID organizationId,
            @PathVariable UUID contractId) {
        VendorContract contract = useCase.terminateContract(organizationId, contractId);
        return ResponseEntity.ok(toResponse(contract));
    }

    @GetMapping("/renewal-notices")
    public ResponseEntity<List<ContractResponse>> getRenewalNotices(@PathVariable UUID organizationId) {
        List<ContractResponse> list = useCase.getContractsRequiringRenewalNotice(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
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
                c.getNoticePeriodDays(),
                c.getStatus().name(),
                c.isExpired(LocalDate.now())
        );
    }
}
