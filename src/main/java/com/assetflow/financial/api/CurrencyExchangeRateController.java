package com.assetflow.financial.api;

import com.assetflow.financial.application.CurrencyExchangeRateUseCase;
import com.assetflow.financial.domain.CurrencyExchangeRate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/currency-exchange")
public class CurrencyExchangeRateController {

    private final CurrencyExchangeRateUseCase useCase;

    public CurrencyExchangeRateController(CurrencyExchangeRateUseCase useCase) {
        this.useCase = useCase;
    }

    public record SetExchangeRateRequest(
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate
    ) {}

    public record ExchangeRateResponse(
            UUID id,
            UUID organizationId,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate,
            LocalDate effectiveDate
    ) {}

    public record ConversionResponse(
            BigDecimal originalAmount,
            String fromCurrency,
            BigDecimal convertedAmount,
            String toCurrency,
            LocalDate effectiveDate
    ) {}

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> setRate(
            @PathVariable UUID organizationId,
            @RequestBody SetExchangeRateRequest request) {
        CurrencyExchangeRate rate = useCase.setExchangeRate(
                organizationId,
                request.fromCurrency(),
                request.toCurrency(),
                request.exchangeRate(),
                request.effectiveDate()
        );
        return ResponseEntity.ok(toResponse(rate));
    }

    @GetMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(
            @PathVariable UUID organizationId,
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam(required = false) LocalDate date) {
        LocalDate evalDate = date != null ? date : LocalDate.now();
        BigDecimal converted = useCase.convertValue(organizationId, amount, fromCurrency, toCurrency, evalDate);
        return ResponseEntity.ok(new ConversionResponse(amount, fromCurrency.toUpperCase(), converted, toCurrency.toUpperCase(), evalDate));
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRateResponse>> getRates(@PathVariable UUID organizationId) {
        List<ExchangeRateResponse> list = useCase.getRatesForOrganization(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    private ExchangeRateResponse toResponse(CurrencyExchangeRate r) {
        return new ExchangeRateResponse(
                r.getId(),
                r.getOrganizationId(),
                r.getFromCurrency(),
                r.getToCurrency(),
                r.getExchangeRate(),
                r.getEffectiveDate()
        );
    }
}
