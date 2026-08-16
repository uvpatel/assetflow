package com.assetflow.identity.infrastructure.persistence;

import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final UserAccountJpaRepository jpaRepository;

    public UserAccountRepositoryImpl(UserAccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        UserAccountEntity entity = toEntity(userAccount);
        UserAccountEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return jpaRepository.findByEmail(email.trim().toLowerCase()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email.trim().toLowerCase());
    }

    private UserAccountEntity toEntity(UserAccount domain) {
        return new UserAccountEntity(
                domain.getId(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.getOrganizationId(),
                domain.getStatus(),
                domain.getRole(),
                domain.getFailedLoginAttempts(),
                domain.getLockedUntil(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    private UserAccount toDomain(UserAccountEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getOrganizationId(),
                entity.getStatus(),
                entity.getRole(),
                entity.getFailedLoginAttempts(),
                entity.getLockedUntil(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
