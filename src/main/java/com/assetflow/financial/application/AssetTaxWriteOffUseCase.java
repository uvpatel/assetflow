package com.assetflow.financial.application;

import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.financial.domain.AssetTaxWriteOff;
import com.assetflow.financial.domain.AssetTaxWriteOffRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AssetTaxWriteOffUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetTaxWriteOffUseCase.class);

    private final AssetTaxWriteOffRepository repository;
    private final AssetRepository assetRepository;

    public AssetTaxWriteOffUseCase(AssetTaxWriteOffRepository repository, AssetRepository assetRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AssetTaxWriteOff executeWriteOff(
            UUID organizationId,
            UUID assetId,
            int fiscalYear,
            BigDecimal currentTaxBasis,
            BigDecimal writeoffAmount,
            String writeoffReason) {
        AssetTaxWriteOff writeOff = AssetTaxWriteOff.executeWriteOff(
                organizationId,
                assetId,
                fiscalYear,
                currentTaxBasis,
                writeoffAmount,
                writeoffReason
        );

        AssetTaxWriteOff saved = repository.save(writeOff);

        if (saved.getStatus() == AssetTaxWriteOff.WriteOffStatus.APPROVED && saved.getRemainingTaxBasis().compareTo(BigDecimal.ZERO) == 0) {
            markAssetDisposed(assetId);
        }

        log.info("Executed tax write-off {} for asset {} (Status: {})", saved.getId(), assetId, saved.getStatus());
        return saved;
    }

    @Transactional
    public AssetTaxWriteOff approveWriteOff(UUID organizationId, UUID writeOffId, UUID approverUserId) {
        AssetTaxWriteOff writeOff = repository.findById(writeOffId)
                .orElseThrow(() -> new DomainViolationException("Tax Write-Off Not Found", "Target tax write-off was not found."));

        if (!writeOff.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot approve write-off belonging to another organization.");
        }

        writeOff.approve(approverUserId);
        AssetTaxWriteOff saved = repository.save(writeOff);

        if (saved.getRemainingTaxBasis().compareTo(BigDecimal.ZERO) == 0) {
            markAssetDisposed(saved.getAssetId());
        }

        log.info("Approved tax write-off {} for asset {} by user {}", writeOffId, saved.getAssetId(), approverUserId);
        return saved;
    }

    @Transactional
    public AssetTaxWriteOff rejectWriteOff(UUID organizationId, UUID writeOffId, UUID approverUserId) {
        AssetTaxWriteOff writeOff = repository.findById(writeOffId)
                .orElseThrow(() -> new DomainViolationException("Tax Write-Off Not Found", "Target tax write-off was not found."));

        if (!writeOff.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot reject write-off belonging to another organization.");
        }

        writeOff.reject(approverUserId);
        log.info("Rejected tax write-off {} by user {}", writeOffId, approverUserId);
        return repository.save(writeOff);
    }

    @Transactional(readOnly = true)
    public List<AssetTaxWriteOff> getWriteOffHistory(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }

    private void markAssetDisposed(UUID assetId) {
        assetRepository.findById(assetId).ifPresent(asset -> {
            asset.setStatus(AssetStatus.DISPOSED);
            assetRepository.save(asset);
            log.info("Asset {} status updated to DISPOSED following full tax write-off", assetId);
        });
    }
}
