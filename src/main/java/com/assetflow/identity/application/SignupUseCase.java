package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupUseCase {

    private final UserAccountRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public SignupUseCase(UserAccountRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse execute(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DomainViolationException("User with email " + request.email() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        UserAccount account = UserAccount.createEmployee(
                request.email(),
                encodedPassword,
                request.organizationId()
        );

        UserAccount saved = userRepository.save(account);

        return new AuthResponse(
                "dummy_access_token",
                "dummy_refresh_token",
                3600L,
                UserSummaryDto.fromDomain(saved)
        );
    }
}
