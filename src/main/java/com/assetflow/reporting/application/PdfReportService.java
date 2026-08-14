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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PdfReportService {

    private final AuditCycleRepository auditCycleRepository;
    private final AuditItemRepository auditItemRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final AssetRepository assetRepository;

    public PdfReportService(AuditCycleRepository auditCycleRepository,
                            AuditItemRepository auditItemRepository,
                            MaintenanceRequestRepository maintenanceRequestRepository,
                            AssetRepository assetRepository) {
        this.auditCycleRepository = auditCycleRepository;
        this.auditItemRepository = auditItemRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateAuditDiscrepancyAndUtilizationReportPdf() {
        UUID organizationId = TenantContext.requireOrganizationId();

        List<Asset> assets = assetRepository.findByOrganizationId(organizationId);
        Map<UUID, Asset> assetMap = assets.stream().collect(Collectors.toMap(Asset::getId, a -> a));

        List<AuditCycle> auditCycles = auditCycleRepository.findByOrganizationId(organizationId);
        List<MaintenanceRequest> maintenanceRequests = maintenanceRequestRepository.findByOrganizationId(organizationId);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font sectionFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font textFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float y = 750;

                contentStream.beginText();
                contentStream.setFont(titleFont, 16);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("AssetFlow - Audit Discrepancy & Asset Utilization Report");
                contentStream.endText();

                y -= 25;
                contentStream.beginText();
                contentStream.setFont(textFont, 10);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Organization ID: " + organizationId);
                contentStream.endText();

                y -= 30;
                contentStream.beginText();
                contentStream.setFont(sectionFont, 12);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("1. Asset Utilization Overview");
                contentStream.endText();

                long totalCount = assets.size();
                long availableCount = assets.stream().filter(a -> a.getStatus() == AssetStatus.AVAILABLE).count();
                long allocatedCount = assets.stream().filter(a -> a.getStatus() == AssetStatus.ALLOCATED).count();
                long maintenanceCount = assets.stream().filter(a -> a.getStatus() == AssetStatus.UNDER_MAINTENANCE).count();
                long retiredCount = assets.stream().filter(a -> a.getStatus() == AssetStatus.RETIRED).count();

                y -= 20;
                contentStream.beginText();
                contentStream.setFont(textFont, 10);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText(String.format("Total Assets: %d | Available: %d | Allocated: %d | Maintenance: %d | Retired: %d",
                        totalCount, availableCount, allocatedCount, maintenanceCount, retiredCount));
                contentStream.endText();

                y -= 30;
                contentStream.beginText();
                contentStream.setFont(sectionFont, 12);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("2. Audit Discrepancies Summary");
                contentStream.endText();

                int totalDiscrepancies = 0;
                for (AuditCycle cycle : auditCycles) {
                    List<AuditItem> items = auditItemRepository.findByAuditCycleId(cycle.getId());
                    List<AuditItem> discrepancies = items.stream()
                            .filter(i -> "DISCREPANCY".equalsIgnoreCase(i.getVerificationStatus()) || "MISSING".equalsIgnoreCase(i.getVerificationStatus()))
                            .collect(Collectors.toList());

                    totalDiscrepancies += discrepancies.size();

                    y -= 20;
                    if (y < 80) break;

                    contentStream.beginText();
                    contentStream.setFont(textFont, 10);
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(String.format("Cycle: %s (Status: %s) - Discrepancies Found: %d",
                            cycle.getName(), cycle.getStatus(), discrepancies.size()));
                    contentStream.endText();

                    for (AuditItem disc : discrepancies) {
                        y -= 15;
                        if (y < 80) break;
                        Asset asset = assetMap.get(disc.getAssetId());
                        String assetTag = asset != null ? asset.getAssetTag() : disc.getAssetId().toString();
                        contentStream.beginText();
                        contentStream.setFont(textFont, 9);
                        contentStream.newLineAtOffset(65, y);
                        contentStream.showText(String.format("- Asset: %s | Status: %s | Condition: %s | Notes: %s",
                                assetTag, disc.getVerificationStatus(),
                                disc.getVerifiedCondition() != null ? disc.getVerifiedCondition() : "N/A",
                                disc.getNotes() != null ? disc.getNotes() : "None"));
                        contentStream.endText();
                    }
                }

                if (totalDiscrepancies == 0) {
                    y -= 20;
                    contentStream.beginText();
                    contentStream.setFont(textFont, 10);
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText("No audit discrepancies found across recorded cycles.");
                    contentStream.endText();
                }

                y -= 30;
                if (y >= 80) {
                    contentStream.beginText();
                    contentStream.setFont(sectionFont, 12);
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText("3. Maintenance Summary");
                    contentStream.endText();

                    y -= 20;
                    long pendingMaint = maintenanceRequests.stream().filter(m -> "PENDING".equalsIgnoreCase(m.getStatus())).count();
                    long approvedMaint = maintenanceRequests.stream().filter(m -> "APPROVED".equalsIgnoreCase(m.getStatus())).count();
                    long resolvedMaint = maintenanceRequests.stream().filter(m -> "RESOLVED".equalsIgnoreCase(m.getStatus())).count();

                    contentStream.beginText();
                    contentStream.setFont(textFont, 10);
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(String.format("Total Maintenance Requests: %d | Pending: %d | In Progress/Approved: %d | Resolved: %d",
                            maintenanceRequests.size(), pendingMaint, approvedMaint, resolvedMaint));
                    contentStream.endText();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
}
