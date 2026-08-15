package com.assetflow.allocation.application;

import com.assetflow.allocation.api.dto.AcceptCustodyTransferRequest;
import com.assetflow.allocation.api.dto.CustodyTransferResponse;
import com.assetflow.allocation.api.dto.InitiateCustodyTransferRequest;
import com.assetflow.allocation.domain.CustodyTransfer;
import com.assetflow.allocation.domain.CustodyTransferItem;
import com.assetflow.allocation.domain.CustodyTransferItemRepository;
import com.assetflow.allocation.domain.CustodyTransferRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CustodyTransferUseCase {

    private static final Logger log = LoggerFactory.getLogger(CustodyTransferUseCase.class);
    private final CustodyTransferRepository transferRepository;
    private final CustodyTransferItemRepository itemRepository;

    public CustodyTransferUseCase(CustodyTransferRepository transferRepository,
                                  CustodyTransferItemRepository itemRepository) {
        this.transferRepository = transferRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public CustodyTransferResponse initiateTransfer(InitiateCustodyTransferRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        CustodyTransfer transfer = new CustodyTransfer(
                organizationId,
                request.senderEmployeeId(),
                request.recipientEmployeeId(),
                request.notes()
        );
        CustodyTransfer savedTransfer = transferRepository.save(transfer);

        List<CustodyTransferItem> items = request.assetIds().stream()
                .map(assetId -> new CustodyTransferItem(savedTransfer.getId(), assetId, "GOOD", null))
                .toList();

        List<CustodyTransferItem> savedItems = itemRepository.saveAll(items);
        log.info("Initiated custody transfer {} with {} items for organization {}", savedTransfer.getId(), savedItems.size(), organizationId);

        return CustodyTransferResponse.fromDomain(savedTransfer, savedItems);
    }

    @Transactional
    public CustodyTransferResponse acceptTransfer(UUID transferId, AcceptCustodyTransferRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();
        CustodyTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new DomainViolationException("Custody Transfer Not Found", "Target custody transfer was not found."));

        if (!transfer.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot accept transfer belonging to another organization.");
        }

        transfer.accept(request.digitalSignature(), Instant.now());
        CustodyTransfer savedTransfer = transferRepository.save(transfer);

        List<CustodyTransferItem> items = itemRepository.findByCustodyTransferId(savedTransfer.getId());
        log.info("Accepted custody transfer {} with digital signature for organization {}", savedTransfer.getId(), organizationId);

        return CustodyTransferResponse.fromDomain(savedTransfer, items);
    }

    @Transactional
    public CustodyTransferResponse rejectTransfer(UUID transferId) {
        UUID organizationId = TenantContext.requireOrganizationId();
        CustodyTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new DomainViolationException("Custody Transfer Not Found", "Target custody transfer was not found."));

        if (!transfer.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot reject transfer belonging to another organization.");
        }

        transfer.reject();
        CustodyTransfer savedTransfer = transferRepository.save(transfer);

        List<CustodyTransferItem> items = itemRepository.findByCustodyTransferId(savedTransfer.getId());
        log.info("Rejected custody transfer {} for organization {}", savedTransfer.getId(), organizationId);

        return CustodyTransferResponse.fromDomain(savedTransfer, items);
    }

    @Transactional(readOnly = true)
    public List<CustodyTransferResponse> listTransfers() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return transferRepository.findByOrganizationId(organizationId).stream()
                .map(transfer -> {
                    List<CustodyTransferItem> items = itemRepository.findByCustodyTransferId(transfer.getId());
                    return CustodyTransferResponse.fromDomain(transfer, items);
                })
                .toList();
    }
}
