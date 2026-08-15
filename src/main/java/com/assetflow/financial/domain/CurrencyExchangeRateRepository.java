package com.assetflow.financial.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyExchangeRateRepository {
    CurrencyExchangeRate save(CurrencyExchangeRate rate);
    Optional<CurrencyExchangeRate> findLatestRate(UUID organizationId, String fromCurrency, String toCurrency, LocalDate date);
    List<CurrencyExchangeRate> findByOrganizationId(UUID organizationId);
}
