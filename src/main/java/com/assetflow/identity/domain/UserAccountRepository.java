package com.assetflow.identity.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {
    UserAccount save(UserAccount userAccount);
    Optional<UserAccount> findById(UUID id);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
