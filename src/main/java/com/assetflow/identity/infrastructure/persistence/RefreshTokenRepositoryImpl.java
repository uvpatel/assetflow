package com.assetflow.identity.infrastructure.persistence;

import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken domain) {
        RefreshTokenEntity entity = toEntity(domain);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public java.util.List<RefreshToken> findAllActiveByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndRevokedFalse(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId, Instant.now());
    }

    private RefreshTokenEntity toEntity(RefreshToken domain) {
        return new RefreshTokenEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getTokenHash(),
                domain.getExpiresAt(),
                domain.getCreatedAt(),
                domain.isRevoked(),
                domain.getRevokedAt(),
                domain.getReplacedByTokenHash()
        );
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.isRevoked(),
                entity.getRevokedAt(),
                entity.getReplacedByTokenHash()
        );
    }
}
