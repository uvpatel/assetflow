package com.assetflow.financial.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import java.util.UUID;

@Repository
public interface SpringDataCurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRateJpaEntity, UUID> {
    @Query("SELECT r FROM CurrencyExchangeRateJpaEntity r WHERE r.organizationId = :organizationId AND r.fromCurrency = :fromCurrency AND r.toCurrency = :toCurrency AND r.effectiveDate <= :date ORDER BY r.effectiveDate DESC")
    List<CurrencyExchangeRateJpaEntity> findRatesOnOrBefore(UUID organizationId, String fromCurrency, String toCurrency, LocalDate date);

    List<CurrencyExchangeRateJpaEntity> findByOrganizationId(UUID organizationId);
}
