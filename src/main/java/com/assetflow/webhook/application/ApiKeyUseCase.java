package com.assetflow.webhook.application;

import com.assetflow.webhook.domain.ApiKey;
import com.assetflow.webhook.domain.ApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyUseCase {

    private final ApiKeyRepository repository;

    public ApiKeyUseCase(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public record GeneratedApiKey(ApiKey apiKey, String plainTextSecret) {}

    @Transactional
    public GeneratedApiKey createApiKey(
            UUID organizationId,
            String name,
            String scopes,
            Instant expiresAt) {
        String rawRandom = generateRandomToken();
        String prefix = "af_" + rawRandom.substring(0, 8);
        String secretKey = prefix + "." + rawRandom;
        String keyHash = hashKey(secretKey);

        ApiKey apiKey = ApiKey.create(
                organizationId,
                name,
                prefix,
                keyHash,
                scopes,
                expiresAt
        );
        ApiKey saved = repository.save(apiKey);
        return new GeneratedApiKey(saved, secretKey);
    }

    @Transactional
    public void revokeApiKey(UUID apiKeyId) {
        ApiKey apiKey = repository.findById(apiKeyId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + apiKeyId));
        apiKey.revoke();
        repository.save(apiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKey> getApiKeys(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashKey(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }
}
