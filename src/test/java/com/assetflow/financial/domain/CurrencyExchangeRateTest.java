package com.assetflow.financial.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyExchangeRateTest {

    @Test
    void shouldConvertAmountCorrectly() {
        UUID orgId = UUID.randomUUID();

        CurrencyExchangeRate rate = CurrencyExchangeRate.create(
                orgId,
                "USD",
                "EUR",
                new BigDecimal("0.92"),
                LocalDate.now()
        );

        assertThat(rate.getId()).isNotNull();
        assertThat(rate.getFromCurrency()).isEqualTo("USD");
        assertThat(rate.getToCurrency()).isEqualTo("EUR");

        BigDecimal converted = rate.convertAmount(new BigDecimal("100.00"));
        assertThat(converted).isEqualTo(new BigDecimal("92.00"));
    }

    @Test
    void shouldRejectInvalidCurrencyCodeOrZeroRate() {
        UUID orgId = UUID.randomUUID();

        assertThatThrownBy(() -> CurrencyExchangeRate.create(
                orgId, "US", "EUR", new BigDecimal("0.92"), LocalDate.now()
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("3-character ISO code");

        assertThatThrownBy(() -> CurrencyExchangeRate.create(
                orgId, "USD", "EUR", new BigDecimal("0.00"), LocalDate.now()
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("greater than zero");
    }
}
