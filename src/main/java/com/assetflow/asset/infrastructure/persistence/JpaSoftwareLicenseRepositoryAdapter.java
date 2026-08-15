package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.SoftwareLicense;
import com.assetflow.asset.domain.SoftwareLicenseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSoftwareLicenseRepositoryAdapter implements SoftwareLicenseRepository {

    private final SpringDataSoftwareLicenseRepository repository;

    public JpaSoftwareLicenseRepositoryAdapter(SpringDataSoftwareLicenseRepository repository) {
        this.repository = repository;
    }

    @Override
    public SoftwareLicense save(SoftwareLicense license) {
        SoftwareLicenseJpaEntity entity = toEntity(license);
        SoftwareLicenseJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SoftwareLicense> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SoftwareLicense> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private SoftwareLicenseJpaEntity toEntity(SoftwareLicense domain) {
        SoftwareLicenseJpaEntity entity = new SoftwareLicenseJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setSoftwareName(domain.getSoftwareName());
        entity.setLicenseKey(domain.getLicenseKey());
        entity.setTotalSeats(domain.getTotalSeats());
        entity.setAllocatedSeats(domain.getAllocatedSeats());
        entity.setExpirationDate(domain.getExpirationDate());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private SoftwareLicense toDomain(SoftwareLicenseJpaEntity entity) {
        return new SoftwareLicense(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getSoftwareName(),
                entity.getLicenseKey(),
                entity.getTotalSeats(),
                entity.getAllocatedSeats(),
                entity.getExpirationDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
