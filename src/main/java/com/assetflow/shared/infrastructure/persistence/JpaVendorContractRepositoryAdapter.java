package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.VendorContract;
import com.assetflow.shared.domain.VendorContractRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaVendorContractRepositoryAdapter implements VendorContractRepository {

    private final SpringDataVendorContractRepository repository;

    public JpaVendorContractRepositoryAdapter(SpringDataVendorContractRepository repository) {
        this.repository = repository;
    }

    @Override
    public VendorContract save(VendorContract contract) {
        VendorContractJpaEntity entity = toEntity(contract);
        VendorContractJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<VendorContract> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<VendorContract> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<VendorContract> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private VendorContractJpaEntity toEntity(VendorContract domain) {
        VendorContractJpaEntity entity = new VendorContractJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setVendorId(domain.getVendorId());
        entity.setContractNumber(domain.getContractNumber());
        entity.setContractType(domain.getContractType());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setContractValue(domain.getContractValue());
        entity.setAutoRenew(domain.isAutoRenew());
        entity.setNoticePeriodDays(domain.getNoticePeriodDays());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private VendorContract toDomain(VendorContractJpaEntity entity) {
        VendorContract.ContractStatus status = VendorContract.ContractStatus.ACTIVE;
        if (entity.getStatus() != null) {
            try {
                status = VendorContract.ContractStatus.valueOf(entity.getStatus().toUpperCase());
            } catch (Exception ignored) {
            }
        }

        return new VendorContract(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getVendorId(),
                entity.getContractNumber(),
                entity.getContractType(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getContractValue(),
                entity.isAutoRenew(),
                entity.getNoticePeriodDays() > 0 ? entity.getNoticePeriodDays() : 30,
                status,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
