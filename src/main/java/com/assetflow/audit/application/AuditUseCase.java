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
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditUseCase {

    private final AuditCycleRepository auditCycleRepository;
    private final AuditItemRepository auditItemRepository;
    private final AssetRepository assetRepository;

    public AuditUseCase(AuditCycleRepository auditCycleRepository,
                        AuditItemRepository auditItemRepository,
                        AssetRepository assetRepository) {
        this.auditCycleRepository = auditCycleRepository;
        this.auditItemRepository = auditItemRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AuditCycle createAuditCycle(CreateAuditCycleDto dto) {
        UUID organizationId = TenantContext.requireOrganizationId();

        AuditCycle cycle = new AuditCycle(organizationId, dto.getName(), dto.getDescription());
        AuditCycle savedCycle = auditCycleRepository.save(cycle);

        List<Asset> orgAssets = assetRepository.findByOrganizationId(organizationId);
        List<AuditItem> snapshotItems = orgAssets.stream()
                .map(asset -> new AuditItem(savedCycle.getId(), asset.getId()))
                .collect(Collectors.toList());

        auditItemRepository.saveAll(snapshotItems);

        return savedCycle;
    }

    @Transactional
    public AuditItem verifyItem(UUID itemId, VerifyAuditItemDto dto) {
        AuditItem item = auditItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditItem", itemId));

        AuditCycle cycle = auditCycleRepository.findById(item.getAuditCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("AuditCycle", item.getAuditCycleId()));

        if (cycle.isClosed()) {
            throw new DomainViolationException("Cannot verify items in a closed audit cycle");
        }

        item.verify(dto.getVerifiedCondition(), dto.getVerificationStatus(), dto.getNotes());
        return auditItemRepository.save(item);
    }

    @Transactional
    public AuditCycle closeCycle(UUID cycleId) {
        AuditCycle cycle = auditCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditCycle", cycleId));

        if (cycle.isClosed()) {
            throw new DomainViolationException("Audit cycle is already closed");
        }

        cycle.close();
        return auditCycleRepository.save(cycle);
    }

    @Transactional(readOnly = true)
    public List<AuditCycle> listCycles() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return auditCycleRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<AuditItem> listItems(UUID cycleId) {
        return auditItemRepository.findByAuditCycleId(cycleId);
    }
}
