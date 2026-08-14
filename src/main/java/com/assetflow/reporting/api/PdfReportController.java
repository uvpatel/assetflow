package com.assetflow.reporting.api;

import com.assetflow.reporting.application.CsvExportService;
import com.assetflow.reporting.application.PdfReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporting")
public class PdfReportController {

    private final CsvExportService csvExportService;
    private final PdfReportService pdfReportService;

    public PdfReportController(CsvExportService csvExportService, PdfReportService pdfReportService) {
        this.csvExportService = csvExportService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping("/export/assets/csv")
    public ResponseEntity<String> exportAssetsCsv() {
        String csvData = csvExportService.exportAssetsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"assets-export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/export/audit-summary/pdf")
    public ResponseEntity<byte[]> exportAuditSummaryPdf() {
        byte[] pdfBytes = pdfReportService.generateAuditDiscrepancyAndUtilizationReportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit-summary-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
