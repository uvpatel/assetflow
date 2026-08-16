package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetDepreciationUseCase;
import com.assetflow.asset.domain.AssetDepreciationPeriodClosingRun;
import com.assetflow.asset.domain.AssetDepreciationSchedule;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetDepreciationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AssetDepreciationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssetDepreciationUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create asset depreciation schedule via POST")
    void shouldCreateDepreciationSchedule() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDepreciationSchedule schedule = new AssetDepreciationSchedule(
                UUID.randomUUID(),
                orgId,
                assetId,
                AssetDepreciationSchedule.DepreciationMethod.STRAIGHT_LINE,
                new BigDecimal("12000.00"),
                new BigDecimal("2000.00"),
                60,
                new BigDecimal("12000.00"),
                BigDecimal.ZERO,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        when(useCase.createSchedule(
                eq(orgId), eq(assetId), eq("STRAIGHT_LINE"),
                eq(new BigDecimal("12000.00")), eq(new BigDecimal("2000.00")), eq(60)
        )).thenReturn(schedule);

        AssetDepreciationController.CreateDepreciationRequest request =
                new AssetDepreciationController.CreateDepreciationRequest(
                        "STRAIGHT_LINE",
                        new BigDecimal("12000.00"),
                        new BigDecimal("2000.00"),
                        60
                );

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/assets/{assetId}/depreciation", orgId, assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("STRAIGHT_LINE"))
                .andExpect(jsonPath("$.initialCost").value(12000.00))
                .andExpect(jsonPath("$.usefulLifeMonths").value(60));
    }

    @Test
    @DisplayName("Should calculate depreciation via POST")
    void shouldCalculateDepreciation() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDepreciationSchedule schedule = new AssetDepreciationSchedule(
                UUID.randomUUID(),
                orgId,
                assetId,
                AssetDepreciationSchedule.DepreciationMethod.STRAIGHT_LINE,
                new BigDecimal("12000.00"),
                new BigDecimal("2000.00"),
                60,
                new BigDecimal("10000.00"),
                new BigDecimal("2000.00"),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        when(useCase.calculateDepreciation(eq(orgId), eq(assetId), eq(12))).thenReturn(schedule);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/assets/{assetId}/depreciation/calculate", orgId, assetId)
                        .param("elapsedMonths", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBookValue").value(10000.00))
                .andExpect(jsonPath("$.accumulatedDepreciation").value(2000.00));
    }

    @Test
    @DisplayName("Should run period closing via POST")
    void shouldRunPeriodClosing() throws Exception {
        UUID orgId = UUID.randomUUID();

        AssetDepreciationPeriodClosingRun run = new AssetDepreciationPeriodClosingRun(
                orgId,
                2026,
                8,
                5,
                new BigDecimal("1500.00")
        );

        when(useCase.runPeriodClosing(eq(orgId), eq(2026), eq(8), eq(5))).thenReturn(run);

        AssetDepreciationController.RunPeriodClosingRequest request =
                new AssetDepreciationController.RunPeriodClosingRequest(2026, 8, 5);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/depreciation/period-close", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodYear").value(2026))
                .andExpect(jsonPath("$.periodMonth").value(8))
                .andExpect(jsonPath("$.processedSchedulesCount").value(5))
                .andExpect(jsonPath("$.totalDepreciationAmount").value(1500.00));
    }

    @Test
    @DisplayName("Should fetch period closing runs via GET")
    void shouldGetPeriodClosingRuns() throws Exception {
        UUID orgId = UUID.randomUUID();

        AssetDepreciationPeriodClosingRun run = new AssetDepreciationPeriodClosingRun(
                orgId,
                2026,
                7,
                4,
                new BigDecimal("1200.00")
        );

        when(useCase.getPeriodClosingRuns(eq(orgId))).thenReturn(List.of(run));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/depreciation/period-closing-runs", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].periodYear").value(2026))
                .andExpect(jsonPath("$[0].periodMonth").value(7));
    }
}
