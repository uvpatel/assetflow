package com.assetflow.maintenance.infrastructure;

import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataMaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {
    List<MaintenanceRequest> findByOrganizationId(UUID organizationId);
}

@Repository
public class JpaMaintenanceRequestRepositoryAdapter implements MaintenanceRequestRepository {

    private final SpringDataMaintenanceRequestRepository repository;

    public JpaMaintenanceRequestRepositoryAdapter(SpringDataMaintenanceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public MaintenanceRequest save(MaintenanceRequest request) {
        return repository.save(request);
    }

    @Override
    public Optional<MaintenanceRequest> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<MaintenanceRequest> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
