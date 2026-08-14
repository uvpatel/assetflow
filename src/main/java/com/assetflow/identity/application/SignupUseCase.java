package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupUseCase {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupUseCase(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse execute(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DomainViolationException("User with email " + request.getEmail() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserAccount account = UserAccount.createEmployee(
                request.getEmail(),
                encodedPassword,
                request.getOrganizationId()
        );

        UserAccount saved = userRepository.save(account);

        return new AuthResponse(
                saved.getId(),
                saved.getOrganizationId(),
                saved.getEmail(),
                saved.getRole(),
                saved.getStatus(),
                "dummy_access_token",
                "dummy_refresh_token"
        );
    }
}
