package com.assetflow.reporting.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.audit.domain.AuditItem;
import com.assetflow.audit.domain.AuditItemRepository;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfReportServiceTest {

    @Mock
    private AuditCycleRepository auditCycleRepository;

    @Mock
    private AuditItemRepository auditItemRepository;

    @Mock
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private PdfReportService pdfReportService;

    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testGenerateAuditDiscrepancyAndUtilizationReportPdf() {
        Asset asset1 = new Asset(orgId, UUID.randomUUID(), "TAG-001", "Laptop 1", "Workstation", "SN001", "Building A");
        asset1.setId(UUID.randomUUID());
        asset1.setStatus(AssetStatus.ALLOCATED);

        Asset asset2 = new Asset(orgId, UUID.randomUUID(), "TAG-002", "Monitor 1", "27 Inch", "SN002", "Building B");
        asset2.setId(UUID.randomUUID());
        asset2.setStatus(AssetStatus.AVAILABLE);

        when(assetRepository.findByOrganizationId(orgId)).thenReturn(List.of(asset1, asset2));

        AuditCycle cycle = new AuditCycle(orgId, "Q1 Audit", "Quarterly Inventory Audit");
        cycle.setId(UUID.randomUUID());
        when(auditCycleRepository.findByOrganizationId(orgId)).thenReturn(List.of(cycle));

        AuditItem item1 = new AuditItem(cycle.getId(), asset1.getId());
        item1.verify("POOR", "DISCREPANCY", "Damaged screen");
        when(auditItemRepository.findByAuditCycleId(cycle.getId())).thenReturn(List.of(item1));

        MaintenanceRequest req = new MaintenanceRequest(orgId, asset1.getId(), UUID.randomUUID(), "Screen repair", "Fix broken LCD", "HIGH");
        when(maintenanceRequestRepository.findByOrganizationId(orgId)).thenReturn(List.of(req));

        byte[] pdfBytes = pdfReportService.generateAuditDiscrepancyAndUtilizationReportPdf();

        assertThat(pdfBytes).isNotEmpty();
        assertThat(new String(pdfBytes)).contains("%PDF");
    }
}
