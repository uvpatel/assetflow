package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetWarrantyDocumentTest {

    @Test
    @DisplayName("Should create warranty document attachment with valid metadata")
    void shouldCreateWarrantyDocument() {
        UUID orgId = UUID.randomUUID();
        UUID warrantyId = UUID.randomUUID();

        AssetWarrantyDocument doc = new AssetWarrantyDocument(
                orgId,
                warrantyId,
                "Warranty_Policy_2026.pdf",
                "application/pdf",
                1024500L,
                "s3://bucket/warranty_policy_2026.pdf"
        );

        assertThat(doc.getOrganizationId()).isEqualTo(orgId);
        assertThat(doc.getWarrantyId()).isEqualTo(warrantyId);
        assertThat(doc.getDocumentName()).isEqualTo("Warranty_Policy_2026.pdf");
        assertThat(doc.getFileSizeBytes()).isEqualTo(1024500L);
    }

    @Test
    @DisplayName("Should reject invalid zero or negative file size")
    void shouldRejectInvalidFileSize() {
        UUID orgId = UUID.randomUUID();
        UUID warrantyId = UUID.randomUUID();

        assertThatThrownBy(() -> new AssetWarrantyDocument(
                orgId, warrantyId, "Doc.pdf", "pdf", 0L, "s3://url"
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("greater than zero");
    }
}
