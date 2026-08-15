package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.Organization;
import com.assetflow.organization.domain.OrganizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataOrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByCode(String code);
    boolean existsByCode(String code);
}

@Repository
public class JpaOrganizationRepositoryAdapter implements OrganizationRepository {

    private final SpringDataOrganizationRepository repository;

    public JpaOrganizationRepositoryAdapter(SpringDataOrganizationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Organization save(Organization organization) {
        return repository.save(organization);
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Organization> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
