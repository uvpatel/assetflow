package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.AssetAllocation;
import com.assetflow.allocation.domain.AssetAllocationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataAssetAllocationRepository extends JpaRepository<AssetAllocation, UUID> {
    List<AssetAllocation> findByOrganizationId(UUID organizationId);

    @Query("SELECT a FROM AssetAllocation a WHERE a.assetId = :assetId AND a.returnedAt IS NULL")
    Optional<AssetAllocation> findActiveByAssetId(UUID assetId);

    @Query("SELECT a FROM AssetAllocation a WHERE a.returnedAt IS NULL AND a.expectedReturnAt IS NOT NULL AND a.expectedReturnAt < :now")
    List<AssetAllocation> findOverdueAllocations(java.time.Instant now);
}

@Repository
public class JpaAssetAllocationRepositoryAdapter implements AssetAllocationRepository {

    private final SpringDataAssetAllocationRepository repository;

    public JpaAssetAllocationRepositoryAdapter(SpringDataAssetAllocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetAllocation save(AssetAllocation allocation) {
        return repository.save(allocation);
    }

    @Override
    public Optional<AssetAllocation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<AssetAllocation> findActiveByAssetId(UUID assetId) {
        return repository.findActiveByAssetId(assetId);
    }

    @Override
    public List<AssetAllocation> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public List<AssetAllocation> findOverdueAllocations(java.time.Instant now) {
        return repository.findOverdueAllocations(now);
    }
}
