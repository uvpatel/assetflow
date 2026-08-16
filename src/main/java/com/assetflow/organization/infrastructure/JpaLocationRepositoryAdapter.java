package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.Location;
import com.assetflow.organization.domain.LocationRepository;
import com.assetflow.organization.domain.LocationType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class JpaLocationRepositoryAdapter implements LocationRepository {

    private final SpringDataLocationRepository springDataRepository;

    public JpaLocationRepositoryAdapter(SpringDataLocationRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Location save(Location location) {
        LocationJpaEntity entity = toEntity(location);
        LocationJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Location> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Location> findByOrganizationIdAndCode(UUID organizationId, String code) {
        return springDataRepository.findByOrganizationIdAndCode(organizationId, code).map(this::toDomain);
    }

    @Override
    public List<Location> findByOrganizationId(UUID organizationId) {
        return springDataRepository.findByOrganizationIdOrderByCodeAsc(organizationId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Location> findByOrganizationIdAndParentLocationId(UUID organizationId, UUID parentLocationId) {
        return springDataRepository.findByOrganizationIdAndParentLocationIdOrderByCodeAsc(organizationId, parentLocationId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Location> findByOrganizationIdAndType(UUID organizationId, LocationType type) {
        return springDataRepository.findByOrganizationIdAndTypeOrderByCodeAsc(organizationId, type).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndCode(UUID organizationId, String code) {
        return springDataRepository.existsByOrganizationIdAndCode(organizationId, code);
    }

    private LocationJpaEntity toEntity(Location d) {
        return new LocationJpaEntity(
                d.getId(),
                d.getOrganizationId(),
                d.getName(),
                d.getCode(),
                d.getType(),
                d.getParentLocationId(),
                d.getAddress(),
                d.getDescription(),
                d.isActive(),
                d.getCreatedAt(),
                d.getUpdatedAt(),
                null,
                null,
                d.getVersion()
        );
    }

    private Location toDomain(LocationJpaEntity e) {
        return new Location(
                e.getId(),
                e.getOrganizationId(),
                e.getName(),
                e.getCode(),
                e.getType(),
                e.getParentLocationId(),
                e.getAddress(),
                e.getDescription(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getVersion()
        );
    }
}
