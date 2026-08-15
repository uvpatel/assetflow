package com.assetflow.financial.infrastructure.persistence;

import com.assetflow.financial.domain.CurrencyExchangeRate;
import com.assetflow.financial.domain.CurrencyExchangeRateRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCurrencyExchangeRateRepositoryAdapter implements CurrencyExchangeRateRepository {

    private final SpringDataCurrencyExchangeRateRepository repository;

    public JpaCurrencyExchangeRateRepositoryAdapter(SpringDataCurrencyExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public CurrencyExchangeRate save(CurrencyExchangeRate rate) {
        CurrencyExchangeRateJpaEntity entity = toEntity(rate);
        CurrencyExchangeRateJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CurrencyExchangeRate> findLatestRate(UUID organizationId, String fromCurrency, String toCurrency, LocalDate date) {
        return repository.findRatesOnOrBefore(organizationId, fromCurrency.toUpperCase(), toCurrency.toUpperCase(), date)
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<CurrencyExchangeRate> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private CurrencyExchangeRateJpaEntity toEntity(CurrencyExchangeRate domain) {
        CurrencyExchangeRateJpaEntity entity = new CurrencyExchangeRateJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setFromCurrency(domain.getFromCurrency());
        entity.setToCurrency(domain.getToCurrency());
        entity.setExchangeRate(domain.getExchangeRate());
        entity.setEffectiveDate(domain.getEffectiveDate());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private CurrencyExchangeRate toDomain(CurrencyExchangeRateJpaEntity entity) {
        return new CurrencyExchangeRate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getFromCurrency(),
                entity.getToCurrency(),
                entity.getExchangeRate(),
                entity.getEffectiveDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
