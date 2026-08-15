package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.infrastructure.security.JwtProperties;
import com.assetflow.shared.error.DomainViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SignupUseCase {

    private final UserAccountRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final JwtProperties jwtProperties;

    public SignupUseCase(UserAccountRepository userRepository,
                          PasswordEncoderPort passwordEncoder,
                          TokenProviderPort tokenProvider,
                          JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
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
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = UUID.randomUUID().toString();

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationSeconds(),
                UserSummaryDto.fromDomain(saved)
        );
    }
}

