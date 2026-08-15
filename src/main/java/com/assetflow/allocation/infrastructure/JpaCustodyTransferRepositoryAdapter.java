package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.CustodyTransfer;
import com.assetflow.allocation.domain.CustodyTransferRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCustodyTransferRepositoryAdapter implements CustodyTransferRepository {

    private final CustodyTransferJpaRepository jpaRepository;

    public JpaCustodyTransferRepositoryAdapter(CustodyTransferJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustodyTransfer save(CustodyTransfer transfer) {
        return jpaRepository.save(transfer);
    }

    @Override
    public Optional<CustodyTransfer> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CustodyTransfer> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findByOrganizationId(organizationId);
    }

    @Override
    public List<CustodyTransfer> findByOrganizationIdAndRecipientEmployeeId(UUID organizationId, UUID recipientEmployeeId) {
        return jpaRepository.findByOrganizationIdAndRecipientEmployeeId(organizationId, recipientEmployeeId);
    }
}
