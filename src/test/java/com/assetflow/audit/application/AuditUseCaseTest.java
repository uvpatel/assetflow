package com.assetflow.audit.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.audit.api.dto.CreateAuditCycleDto;
import com.assetflow.audit.api.dto.VerifyAuditItemDto;
import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.audit.domain.AuditItem;
import com.assetflow.audit.domain.AuditItemRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditUseCaseTest {

    @Mock
    private AuditCycleRepository auditCycleRepository;

    @Mock
    private AuditItemRepository auditItemRepository;

    @Mock
    private AssetRepository assetRepository;

    private AuditUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AuditUseCase(auditCycleRepository, auditItemRepository, assetRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should create audit cycle and snapshot organization assets as audit items")
    void shouldCreateAuditCycleAndSnapshotItems() {
        Asset asset1 = new Asset(orgId, UUID.randomUUID(), "TAG-1", "Laptop", "Desc", "SN1", "Loc1");
        asset1.setId(UUID.randomUUID());

        when(assetRepository.findByOrganizationId(orgId)).thenReturn(List.of(asset1));
        when(auditCycleRepository.save(any(AuditCycle.class))).thenAnswer(inv -> {
            AuditCycle c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CreateAuditCycleDto dto = new CreateAuditCycleDto();
        dto.setName("Annual Inventory Audit 2026");
        dto.setDescription("Full asset scan");

        AuditCycle cycle = useCase.createAuditCycle(dto);

        assertThat(cycle.getName()).isEqualTo("Annual Inventory Audit 2026");
        assertThat(cycle.getStatus()).isEqualTo("OPEN");
        verify(auditItemRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should verify audit item condition and status")
    void shouldVerifyAuditItem() {
        UUID cycleId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        AuditCycle cycle = new AuditCycle(orgId, "Q3 Audit", "Desc");
        cycle.setId(cycleId);

        AuditItem item = new AuditItem(cycleId, UUID.randomUUID());
        item.setId(itemId);

        when(auditItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(auditCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));
        when(auditItemRepository.save(any(AuditItem.class))).thenAnswer(inv -> inv.getArgument(0));

        VerifyAuditItemDto dto = new VerifyAuditItemDto();
        dto.setVerifiedCondition("GOOD");
        dto.setVerificationStatus("VERIFIED");
        dto.setNotes("Scanned in Rack 4");

        AuditItem verified = useCase.verifyItem(itemId, dto);

        assertThat(verified.getVerificationStatus()).isEqualTo("VERIFIED");
        assertThat(verified.getVerifiedCondition()).isEqualTo("GOOD");
    }

    @Test
    @DisplayName("Should auto-flag pending items as MISSING upon closing audit cycle")
    void shouldFlagMissingItemsOnClose() {
        UUID cycleId = UUID.randomUUID();
        AuditCycle cycle = new AuditCycle(orgId, "Q3 Audit", "Desc");
        cycle.setId(cycleId);

        AuditItem pendingItem = new AuditItem(cycleId, UUID.randomUUID());

        when(auditCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));
        when(auditItemRepository.findByAuditCycleId(cycleId)).thenReturn(List.of(pendingItem));
        when(auditCycleRepository.save(any(AuditCycle.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditCycle closed = useCase.closeCycle(cycleId);

        assertThat(closed.isClosed()).isTrue();
        assertThat(pendingItem.getVerificationStatus()).isEqualTo("MISSING");
        verify(auditItemRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should reject verifying item in a closed audit cycle")
    void shouldRejectVerificationInClosedCycle() {
        UUID cycleId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        AuditCycle cycle = new AuditCycle(orgId, "Q3 Audit", "Desc");
        cycle.setId(cycleId);
        cycle.close();

        AuditItem item = new AuditItem(cycleId, UUID.randomUUID());
        item.setId(itemId);

        when(auditItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(auditCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));

        VerifyAuditItemDto dto = new VerifyAuditItemDto();
        dto.setVerificationStatus("VERIFIED");

        assertThatThrownBy(() -> useCase.verifyItem(itemId, dto))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("closed");
    }
}
