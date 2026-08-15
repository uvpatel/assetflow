package com.assetflow.financial.application;

import com.assetflow.financial.domain.CurrencyExchangeRate;
import com.assetflow.financial.domain.CurrencyExchangeRateRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CurrencyExchangeRateUseCase {

    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeRateUseCase.class);

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
            LocalDate effectiveDate,
            String rateSourceStr) {
        CurrencyExchangeRate.RateSource rateSource = (rateSourceStr != null && !rateSourceStr.isBlank())
                ? CurrencyExchangeRate.RateSource.valueOf(rateSourceStr.toUpperCase())
                : CurrencyExchangeRate.RateSource.MANUAL;

        CurrencyExchangeRate rate = CurrencyExchangeRate.create(
                organizationId,
                fromCurrency,
                toCurrency,
                exchangeRate,
                effectiveDate,
                rateSource
        );
        log.info("Configured FX rate {}/{} = {} (Source: {})", fromCurrency, toCurrency, exchangeRate, rateSource);
        return repository.save(rate);
    }

    @Transactional
    public CurrencyExchangeRate setExchangeRate(
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate) {
        return setExchangeRate(organizationId, fromCurrency, toCurrency, exchangeRate, effectiveDate, "MANUAL");
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

        Optional<CurrencyExchangeRate> directRate = repository.findLatestRate(organizationId, fromCurrency, toCurrency, evalDate);
        if (directRate.isPresent()) {
            return directRate.get().convertAmount(amount);
        }

        Optional<CurrencyExchangeRate> inverseRate = repository.findLatestRate(organizationId, toCurrency, fromCurrency, evalDate);
        if (inverseRate.isPresent()) {
            BigDecimal inverse = inverseRate.get().getInverseRate();
            return amount.multiply(inverse).setScale(2, RoundingMode.HALF_UP);
        }

        throw new DomainViolationException("Exchange Rate Not Found", "No exchange rate found for " + fromCurrency + " to " + toCurrency + " on or before " + evalDate);
    }

    @Transactional(readOnly = true)
    public List<CurrencyExchangeRate> getRatesForOrganization(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
