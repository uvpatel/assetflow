package com.assetflow.financial.api;

import com.assetflow.financial.application.AssetTaxWriteOffUseCase;
import com.assetflow.financial.domain.AssetTaxWriteOff;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetTaxWriteOffController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AssetTaxWriteOffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssetTaxWriteOffUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should execute tax write-off request via POST")
    void shouldExecuteWriteOff() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetTaxWriteOff writeOff = new AssetTaxWriteOff(
                UUID.randomUUID(),
                orgId,
                assetId,
                2026,
                new BigDecimal("5000.00"),
                new BigDecimal("20000.00"),
                "Annual obsolescence",
                AssetTaxWriteOff.WriteOffStatus.APPROVED,
                null,
                null,
                Instant.now()
        );

        when(useCase.executeWriteOff(
                eq(orgId), eq(assetId), eq(2026),
                eq(new BigDecimal("25000.00")), eq(new BigDecimal("5000.00")), eq("Annual obsolescence")
        )).thenReturn(writeOff);

        AssetTaxWriteOffController.WriteOffRequest request = new AssetTaxWriteOffController.WriteOffRequest(
                2026,
                new BigDecimal("25000.00"),
                new BigDecimal("5000.00"),
                "Annual obsolescence"
        );

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/assets/{assetId}/tax-writeoffs", orgId, assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiscalYear").value(2026))
                .andExpect(jsonPath("$.writeoffAmount").value(5000.00))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Should get write-off history for asset via GET")
    void shouldGetWriteOffHistory() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetTaxWriteOff writeOff = new AssetTaxWriteOff(
                UUID.randomUUID(),
                orgId,
                assetId,
                2025,
                new BigDecimal("3000.00"),
                new BigDecimal("22000.00"),
                "Year 1 write-off",
                AssetTaxWriteOff.WriteOffStatus.APPROVED,
                null,
                null,
                Instant.now()
        );

        when(useCase.getWriteOffHistory(orgId, assetId)).thenReturn(List.of(writeOff));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/assets/{assetId}/tax-writeoffs", orgId, assetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fiscalYear").value(2025))
                .andExpect(jsonPath("$[0].writeoffAmount").value(3000.00));
    }

    @Test
    @DisplayName("Should approve pending tax write-off via POST")
    void shouldApproveWriteOff() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID writeOffId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        AssetTaxWriteOff approved = new AssetTaxWriteOff(
                writeOffId,
                orgId,
                UUID.randomUUID(),
                2026,
                new BigDecimal("15000.00"),
                new BigDecimal("10000.00"),
                "High value retirement",
                AssetTaxWriteOff.WriteOffStatus.APPROVED,
                approverId,
                Instant.now(),
                Instant.now()
        );

        when(useCase.approveWriteOff(orgId, writeOffId, approverId)).thenReturn(approved);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/tax-writeoffs/{id}/approve", orgId, writeOffId)
                        .param("approverUserId", approverId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approvedBy").value(approverId.toString()));
    }

    @Test
    @DisplayName("Should reject pending tax write-off via POST")
    void shouldRejectWriteOff() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID writeOffId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();

        AssetTaxWriteOff rejected = new AssetTaxWriteOff(
                writeOffId,
                orgId,
                UUID.randomUUID(),
                2026,
                new BigDecimal("15000.00"),
                new BigDecimal("25000.00"),
                "High value retirement",
                AssetTaxWriteOff.WriteOffStatus.REJECTED,
                approverId,
                null,
                Instant.now()
        );

        when(useCase.rejectWriteOff(orgId, writeOffId, approverId)).thenReturn(rejected);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/tax-writeoffs/{id}/reject", orgId, writeOffId)
                        .param("approverUserId", approverId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}
