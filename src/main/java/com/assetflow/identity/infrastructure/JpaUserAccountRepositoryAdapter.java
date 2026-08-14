package com.assetflow.identity.infrastructure;

import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataUserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}

@Repository
public class JpaUserAccountRepositoryAdapter implements UserAccountRepository {

    private final SpringDataUserAccountRepository springDataRepository;

    public JpaUserAccountRepositoryAdapter(SpringDataUserAccountRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        return springDataRepository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return springDataRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }
}
