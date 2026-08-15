package com.assetflow.financial.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CurrencyExchangeRate {

    public enum RateSource {
        MANUAL,
        ECB,
        OPEN_EXCHANGE,
        CENTRAL_BANK
    }

    private final UUID id;
    private final UUID organizationId;
    private final String fromCurrency;
    private final String toCurrency;
    private BigDecimal exchangeRate;
    private final LocalDate effectiveDate;
    private RateSource rateSource;
    private boolean isActive;
    private final Instant createdAt;
    private Instant updatedAt;

    public CurrencyExchangeRate(
            UUID id,
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate,
            RateSource rateSource,
            boolean isActive,
            Instant createdAt,
            Instant updatedAt) {
        if (fromCurrency == null || fromCurrency.isBlank() || fromCurrency.length() != 3) {
            throw new IllegalArgumentException("From currency must be a 3-character ISO code");
        }
        if (toCurrency == null || toCurrency.isBlank() || toCurrency.length() != 3) {
            throw new IllegalArgumentException("To currency must be a 3-character ISO code");
        }
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be greater than zero");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.fromCurrency = fromCurrency.toUpperCase();
        this.toCurrency = toCurrency.toUpperCase();
        this.exchangeRate = exchangeRate;
        this.effectiveDate = effectiveDate;
        this.rateSource = rateSource != null ? rateSource : RateSource.MANUAL;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CurrencyExchangeRate(
            UUID id,
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate,
            Instant createdAt,
            Instant updatedAt) {
        this(id, organizationId, fromCurrency, toCurrency, exchangeRate, effectiveDate, RateSource.MANUAL, true, createdAt, updatedAt);
    }

    public static CurrencyExchangeRate create(
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate,
            RateSource rateSource) {
        Instant now = Instant.now();
        return new CurrencyExchangeRate(
                UUID.randomUUID(),
                organizationId,
                fromCurrency,
                toCurrency,
                exchangeRate,
                effectiveDate != null ? effectiveDate : LocalDate.now(),
                rateSource != null ? rateSource : RateSource.MANUAL,
                true,
                now,
                now
        );
    }

    public static CurrencyExchangeRate create(
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate) {
        return create(organizationId, fromCurrency, toCurrency, exchangeRate, effectiveDate, RateSource.MANUAL);
    }

    public BigDecimal convertAmount(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getInverseRate() {
        return BigDecimal.ONE.divide(exchangeRate, 6, RoundingMode.HALF_UP);
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = Instant.now();
    }

    public void updateRate(BigDecimal newRate) {
        if (newRate == null || newRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be greater than zero");
        }
        this.exchangeRate = newRate;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public RateSource getRateSource() { return rateSource; }
    public boolean isActive() { return isActive; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
