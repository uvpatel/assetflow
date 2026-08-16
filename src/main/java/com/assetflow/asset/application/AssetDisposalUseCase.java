package com.assetflow.asset.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetDisposalRecord;
import com.assetflow.asset.domain.AssetDisposalRecordRepository;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.asset.domain.AssetStatus;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssetDisposalUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssetDisposalUseCase.class);

    private final AssetDisposalRecordRepository disposalRepository;
    private final AssetRepository assetRepository;

    public AssetDisposalUseCase(
            AssetDisposalRecordRepository disposalRepository,
            AssetRepository assetRepository) {
        this.disposalRepository = disposalRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AssetDisposalRecord initiateDisposal(
            UUID organizationId,
            UUID assetId,
            AssetDisposalRecord.DisposalType disposalType,
            String disposalReason,
            BigDecimal proceedsAmount,
            String currency,
            UUID authorizedByUserId,
            String certificateReference) {
        Asset asset = assetRepository.findById(assetId)
                .filter(a -> a.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new DomainViolationException("Asset not found for organization"));

        if (asset.getStatus() == AssetStatus.DISPOSED) {
            throw new DomainViolationException("Asset is already disposed");
        }

        AssetDisposalRecord record = AssetDisposalRecord.initiate(
                organizationId,
                assetId,
                disposalType,
                disposalReason,
                proceedsAmount,
                currency,
                authorizedByUserId,
                certificateReference
        );

        AssetDisposalRecord saved = disposalRepository.save(record);
        log.info("Initiated disposal {} for asset {} in org {}", saved.getId(), assetId, organizationId);
        return saved;
    }

    @Transactional
    public AssetDisposalRecord approveDisposal(UUID organizationId, UUID disposalId, UUID authorizedByUserId) {
        AssetDisposalRecord record = disposalRepository.findByOrganizationIdAndId(organizationId, disposalId)
                .orElseThrow(() -> new DomainViolationException("Disposal record not found"));

        record.approve(authorizedByUserId);
        AssetDisposalRecord saved = disposalRepository.save(record);
        log.info("Approved disposal {} by user {}", disposalId, authorizedByUserId);
        return saved;
    }

    @Transactional
    public AssetDisposalRecord executeDisposal(UUID organizationId, UUID disposalId) {
        AssetDisposalRecord record = disposalRepository.findByOrganizationIdAndId(organizationId, disposalId)
                .orElseThrow(() -> new DomainViolationException("Disposal record not found"));

        Asset asset = assetRepository.findById(record.getAssetId())
                .filter(a -> a.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new DomainViolationException("Associated asset not found"));

        record.execute();
        asset.setStatus(AssetStatus.DISPOSED);

        assetRepository.save(asset);
        AssetDisposalRecord saved = disposalRepository.save(record);

        log.info("Executed disposal {} for asset {}. Status transitioned to DISPOSED", disposalId, asset.getId());
        return saved;
    }

    @Transactional
    public AssetDisposalRecord cancelDisposal(UUID organizationId, UUID disposalId) {
        AssetDisposalRecord record = disposalRepository.findByOrganizationIdAndId(organizationId, disposalId)
                .orElseThrow(() -> new DomainViolationException("Disposal record not found"));

        record.cancel();
        AssetDisposalRecord saved = disposalRepository.save(record);
        log.info("Cancelled disposal record {}", disposalId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AssetDisposalRecord> getDisposalsForOrganization(UUID organizationId) {
        return disposalRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<AssetDisposalRecord> getDisposalsForAsset(UUID organizationId, UUID assetId) {
        return disposalRepository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }

    @Transactional(readOnly = true)
    public Optional<AssetDisposalRecord> getDisposalById(UUID organizationId, UUID disposalId) {
        return disposalRepository.findByOrganizationIdAndId(organizationId, disposalId);
    }
}
