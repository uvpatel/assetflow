package com.assetflow.financial.api;

import com.assetflow.financial.application.AssetDepreciationForecastUseCase;
import com.assetflow.financial.domain.DepreciationForecastProjection;
import com.assetflow.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetDepreciationForecastController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AssetDepreciationForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssetDepreciationForecastUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should generate depreciation forecast for fiscal year")
    void shouldGenerateForecast() throws Exception {
        UUID orgId = UUID.randomUUID();
        int fiscalYear = 2026;
        DepreciationForecastProjection projection = new DepreciationForecastProjection(
                orgId, fiscalYear, new BigDecimal("12000.00"), new BigDecimal("48000.00")
        );

        when(useCase.generateForecast(orgId, fiscalYear)).thenReturn(projection);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/depreciation-forecasts", orgId)
                        .param("fiscalYear", String.valueOf(fiscalYear))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationId").value(orgId.toString()))
                .andExpect(jsonPath("$.fiscalYear").value(fiscalYear))
                .andExpect(jsonPath("$.projectedDepreciation").value(12000.00))
                .andExpect(jsonPath("$.projectedEndingBookValue").value(48000.00));
    }

    @Test
    @DisplayName("Should get all forecasts for organization")
    void shouldGetForecasts() throws Exception {
        UUID orgId = UUID.randomUUID();
        DepreciationForecastProjection projection = new DepreciationForecastProjection(
                orgId, 2026, new BigDecimal("12000.00"), new BigDecimal("48000.00")
        );

        when(useCase.getForecasts(orgId)).thenReturn(List.of(projection));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/depreciation-forecasts", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fiscalYear").value(2026));
    }

    @Test
    @DisplayName("Should get forecast for specific fiscal year")
    void shouldGetForecastForFiscalYear() throws Exception {
        UUID orgId = UUID.randomUUID();
        int fiscalYear = 2026;
        DepreciationForecastProjection projection = new DepreciationForecastProjection(
                orgId, fiscalYear, new BigDecimal("12000.00"), new BigDecimal("48000.00")
        );

        when(useCase.getForecastForFiscalYear(orgId, fiscalYear)).thenReturn(Optional.of(projection));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/depreciation-forecasts/{fiscalYear}", orgId, fiscalYear)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiscalYear").value(fiscalYear));
    }

    @Test
    @DisplayName("Should return 404 when forecast for fiscal year is not found")
    void shouldReturn404WhenForecastNotFound() throws Exception {
        UUID orgId = UUID.randomUUID();

        when(useCase.getForecastForFiscalYear(orgId, 2099)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/depreciation-forecasts/2099", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
