package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.SoftwareLicenseAllocation;
import com.assetflow.asset.domain.SoftwareLicenseAllocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSoftwareLicenseAllocationRepositoryAdapter implements SoftwareLicenseAllocationRepository {

    private final SoftwareLicenseAllocationJpaRepository jpaRepository;

    public JpaSoftwareLicenseAllocationRepositoryAdapter(SoftwareLicenseAllocationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SoftwareLicenseAllocation save(SoftwareLicenseAllocation allocation) {
        return jpaRepository.save(allocation);
    }

    @Override
    public Optional<SoftwareLicenseAllocation> findBySoftwareLicenseIdAndEmployeeId(UUID softwareLicenseId, UUID employeeId) {
        return jpaRepository.findBySoftwareLicenseIdAndEmployeeId(softwareLicenseId, employeeId);
    }

    @Override
    public List<SoftwareLicenseAllocation> findBySoftwareLicenseId(UUID softwareLicenseId) {
        return jpaRepository.findBySoftwareLicenseId(softwareLicenseId);
    }

    @Override
    public void delete(SoftwareLicenseAllocation allocation) {
        jpaRepository.delete(allocation);
    }
}
