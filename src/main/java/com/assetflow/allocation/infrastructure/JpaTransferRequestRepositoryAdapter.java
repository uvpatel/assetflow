package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.TransferRequest;
import com.assetflow.allocation.domain.TransferRequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataTransferRequestRepository extends JpaRepository<TransferRequest, UUID> {
    List<TransferRequest> findByOrganizationId(UUID organizationId);
    List<TransferRequest> findByEmployeeId(UUID employeeId);
}

@Repository
public class JpaTransferRequestRepositoryAdapter implements TransferRequestRepository {

    private final SpringDataTransferRequestRepository repository;

    public JpaTransferRequestRepositoryAdapter(SpringDataTransferRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransferRequest save(TransferRequest transferRequest) {
        return repository.save(transferRequest);
    }

    @Override
    public Optional<TransferRequest> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<TransferRequest> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public List<TransferRequest> findByEmployeeId(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}
