package com.assetflow.financial.application;

import com.assetflow.financial.domain.CurrencyExchangeRate;
import com.assetflow.financial.domain.CurrencyExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CurrencyExchangeRateUseCase {

    private final CurrencyExchangeRateRepository repository;

    public CurrencyExchangeRateUseCase(CurrencyExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CurrencyExchangeRate setExchangeRate(
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate) {
        CurrencyExchangeRate rate = CurrencyExchangeRate.create(
                organizationId,
                fromCurrency,
                toCurrency,
                exchangeRate,
                effectiveDate
        );
        return repository.save(rate);
    }

    @Transactional(readOnly = true)
    public BigDecimal convertValue(
            UUID organizationId,
            BigDecimal amount,
            String fromCurrency,
            String toCurrency,
            LocalDate date) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) return amount;
        LocalDate evalDate = date != null ? date : LocalDate.now();

        return repository.findLatestRate(organizationId, fromCurrency, toCurrency, evalDate)
                .map(r -> r.convertAmount(amount))
                .orElseThrow(() -> new IllegalArgumentException("No exchange rate found for " + fromCurrency + " to " + toCurrency + " on or before " + evalDate));
    }

    @Transactional(readOnly = true)
    public List<CurrencyExchangeRate> getRatesForOrganization(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
