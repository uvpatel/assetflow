package com.assetflow.shared.application;

import com.assetflow.shared.domain.VendorContract;
import com.assetflow.shared.domain.VendorContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VendorContractUseCase {

    private final VendorContractRepository repository;

    public VendorContractUseCase(VendorContractRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VendorContract createContract(
            UUID organizationId,
            UUID vendorId,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal contractValue,
            boolean isAutoRenew) {
        VendorContract contract = VendorContract.create(
                organizationId,
                vendorId,
                contractNumber,
                contractType,
                startDate,
                endDate,
                contractValue,
                isAutoRenew
        );
        return repository.save(contract);
    }

    @Transactional
    public VendorContract renewContract(UUID contractId, LocalDate newEndDate, BigDecimal newContractValue) {
        VendorContract contract = repository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor contract not found: " + contractId));
        contract.renewContract(newEndDate, newContractValue);
        return repository.save(contract);
    }

    @Transactional(readOnly = true)
    public List<VendorContract> getContractsForVendor(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId);
    }

    @Transactional(readOnly = true)
    public List<VendorContract> getExpiringContracts(UUID organizationId, int daysThreshold) {
        LocalDate today = LocalDate.now();
        return repository.findByOrganizationId(organizationId)
                .stream()
                .filter(c -> c.isExpiringWithinDays(today, daysThreshold))
                .toList();
    }
}
