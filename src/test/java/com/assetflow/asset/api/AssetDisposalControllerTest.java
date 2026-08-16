package com.assetflow.asset.api;

import com.assetflow.asset.application.AssetDisposalUseCase;
import com.assetflow.asset.domain.AssetDisposalRecord;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetDisposalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AssetDisposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssetDisposalUseCase useCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should initiate asset disposal via POST")
    void shouldInitiateDisposal() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SALE, "Sold to vendor",
                new BigDecimal("450.00"), "USD", userId, "DISP-101"
        );

        when(useCase.initiateDisposal(
                eq(orgId), eq(assetId), eq(AssetDisposalRecord.DisposalType.SALE),
                eq("Sold to vendor"), eq(new BigDecimal("450.00")), eq("USD"), eq(userId), eq("DISP-101")
        )).thenReturn(record);

        AssetDisposalController.InitiateDisposalRequest request = new AssetDisposalController.InitiateDisposalRequest(
                "SALE", "Sold to vendor", new BigDecimal("450.00"), "USD", userId, "DISP-101"
        );

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/assets/{assetId}/disposals", orgId, assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disposalType").value("SALE"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.proceedsAmount").value(450.00))
                .andExpect(jsonPath("$.certificateReference").value("DISP-101"));
    }

    @Test
    @DisplayName("Should approve disposal record via POST")
    void shouldApproveDisposal() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID disposalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, UUID.randomUUID(), AssetDisposalRecord.DisposalType.SCRAP, "Scrap",
                BigDecimal.ZERO, "USD", null, null
        );
        record.approve(userId);

        when(useCase.approveDisposal(orgId, disposalId, userId)).thenReturn(record);

        AssetDisposalController.ApproveDisposalRequest request = new AssetDisposalController.ApproveDisposalRequest(userId);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/asset-disposals/{disposalId}/approve", orgId, disposalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Should execute disposal record via POST")
    void shouldExecuteDisposal() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID disposalId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, UUID.randomUUID(), AssetDisposalRecord.DisposalType.RECYCLE, "Recycle",
                BigDecimal.ZERO, "USD", null, null
        );
        record.execute();

        when(useCase.executeDisposal(orgId, disposalId)).thenReturn(record);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/asset-disposals/{disposalId}/execute", orgId, disposalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXECUTED"));
    }

    @Test
    @DisplayName("Should cancel disposal record via POST")
    void shouldCancelDisposal() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID disposalId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, UUID.randomUUID(), AssetDisposalRecord.DisposalType.DONATION, "Donation",
                BigDecimal.ZERO, "USD", null, null
        );
        record.cancel();

        when(useCase.cancelDisposal(orgId, disposalId)).thenReturn(record);

        mockMvc.perform(post("/api/v1/organizations/{organizationId}/asset-disposals/{disposalId}/cancel", orgId, disposalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Should list disposal records for asset")
    void shouldListDisposalsForAsset() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, assetId, AssetDisposalRecord.DisposalType.SALE, "Sold",
                new BigDecimal("100.00"), "USD", null, null
        );

        when(useCase.getDisposalsForAsset(orgId, assetId)).thenReturn(List.of(record));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/assets/{assetId}/disposals", orgId, assetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disposalType").value("SALE"));
    }

    @Test
    @DisplayName("Should get single disposal record or return 404")
    void shouldGetDisposalById() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID disposalId = UUID.randomUUID();

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                orgId, UUID.randomUUID(), AssetDisposalRecord.DisposalType.SALE, "Sold",
                new BigDecimal("100.00"), "USD", null, null
        );

        when(useCase.getDisposalById(orgId, disposalId)).thenReturn(Optional.of(record));
        when(useCase.getDisposalById(orgId, UUID.fromString("00000000-0000-0000-0000-000000000000"))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/asset-disposals/{disposalId}", orgId, disposalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disposalType").value("SALE"));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/asset-disposals/{disposalId}", orgId, UUID.fromString("00000000-0000-0000-0000-000000000000"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
