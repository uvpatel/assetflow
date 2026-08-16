package com.assetflow.asset.application;

import com.assetflow.asset.domain.AssetWarranty;
import com.assetflow.asset.domain.AssetWarrantyDocument;
import com.assetflow.asset.domain.AssetWarrantyDocumentRepository;
import com.assetflow.asset.domain.AssetWarrantyRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetWarrantyUseCaseTest {

    @Mock
    private AssetWarrantyRepository repository;

    @Mock
    private AssetWarrantyDocumentRepository documentRepository;

    private AssetWarrantyUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AssetWarrantyUseCase(repository, documentRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should attach warranty document and save to repository")
    void shouldAttachWarrantyDocument() {
        UUID warrantyId = UUID.randomUUID();
        AssetWarranty warranty = AssetWarranty.create(
                orgId, UUID.randomUUID(), "AppleCare", "AC-12345", Instant.now(), Instant.now().plus(365, ChronoUnit.DAYS), "Full hardware coverage"
        );

        when(repository.findById(warrantyId)).thenReturn(Optional.of(warranty));
        when(documentRepository.save(any(AssetWarrantyDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        AssetWarrantyDocument doc = useCase.attachWarrantyDocument(
                orgId, warrantyId, "Invoice.pdf", "application/pdf", 512000L, "s3://docs/invoice.pdf"
        );

        assertThat(doc.getWarrantyId()).isEqualTo(warrantyId);
        assertThat(doc.getDocumentName()).isEqualTo("Invoice.pdf");
        verify(documentRepository).save(any(AssetWarrantyDocument.class));
    }

    @Test
    @DisplayName("Should dispatch warranty expiration alerts for expiring policies")
    void shouldDispatchWarrantyExpiryAlerts() {
        AssetWarranty expiring = AssetWarranty.create(
                orgId, UUID.randomUUID(), "Dell ProSupport", "DELL-99", Instant.now().minus(300, ChronoUnit.DAYS), Instant.now().plus(10, ChronoUnit.DAYS), "Support"
        );

        when(repository.findExpiringWarranties(any(), any())).thenReturn(List.of(expiring));

        int count = useCase.dispatchWarrantyExpiryAlerts(orgId, 30);

        assertThat(count).isEqualTo(1);
    }
}
