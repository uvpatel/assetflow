package com.assetflow.shared.application;

import com.assetflow.shared.domain.VendorContract;
import com.assetflow.shared.domain.VendorContractRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VendorContractUseCase {

    private static final Logger log = LoggerFactory.getLogger(VendorContractUseCase.class);

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
            boolean isAutoRenew,
            int noticePeriodDays) {
        VendorContract contract = VendorContract.create(
                organizationId,
                vendorId,
                contractNumber,
                contractType,
                startDate,
                endDate,
                contractValue,
                isAutoRenew,
                noticePeriodDays
        );
        log.info("Created vendor contract {} for vendor {}", contractNumber, vendorId);
        return repository.save(contract);
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
        return createContract(organizationId, vendorId, contractNumber, contractType, startDate, endDate, contractValue, isAutoRenew, 30);
    }

    @Transactional
    public VendorContract renewContract(UUID contractId, LocalDate newEndDate, BigDecimal newContractValue) {
        VendorContract contract = repository.findById(contractId)
                .orElseThrow(() -> new DomainViolationException("Contract Not Found", "Vendor contract not found: " + contractId));
        contract.renewContract(newEndDate, newContractValue);
        log.info("Renewed contract {} until {}", contractId, newEndDate);
        return repository.save(contract);
    }

    @Transactional
    public VendorContract terminateContract(UUID organizationId, UUID contractId) {
        VendorContract contract = repository.findById(contractId)
                .orElseThrow(() -> new DomainViolationException("Contract Not Found", "Vendor contract not found: " + contractId));

        if (!contract.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot terminate contract belonging to another organization.");
        }

        contract.terminate();
        log.info("Terminated vendor contract {}", contractId);
        return repository.save(contract);
    }

    @Transactional(readOnly = true)
    public List<VendorContract> getContractsRequiringRenewalNotice(UUID organizationId) {
        LocalDate today = LocalDate.now();
        return repository.findByOrganizationId(organizationId)
                .stream()
                .filter(c -> c.shouldSendRenewalNotice(today))
                .toList();
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
