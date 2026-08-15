package com.assetflow.financial.application;

import com.assetflow.financial.domain.CurrencyExchangeRate;
import com.assetflow.financial.domain.CurrencyExchangeRateRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeRateUseCaseTest {

    @Mock
    private CurrencyExchangeRateRepository repository;

    private CurrencyExchangeRateUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CurrencyExchangeRateUseCase(repository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should convert using inverse rate lookup if direct FX rate is not found")
    void shouldConvertUsingInverseRateLookup() {
        LocalDate today = LocalDate.now();
        CurrencyExchangeRate directUsdEur = CurrencyExchangeRate.create(
                orgId, "USD", "EUR", new BigDecimal("0.80"), today
        );

        // Request conversion from EUR to USD (inverse of USD -> EUR)
        when(repository.findLatestRate(orgId, "EUR", "USD", today)).thenReturn(Optional.empty());
        when(repository.findLatestRate(orgId, "USD", "EUR", today)).thenReturn(Optional.of(directUsdEur));

        BigDecimal convertedUsd = useCase.convertValue(orgId, new BigDecimal("100.00"), "EUR", "USD", today);

        // 100 * (1 / 0.80) = 125.00
        assertThat(convertedUsd).isEqualByComparingTo(new BigDecimal("125.00"));
    }
}
