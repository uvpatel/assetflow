package com.assetflow.financial.application;

import com.assetflow.financial.domain.AssetTaxWriteOff;
import com.assetflow.financial.domain.AssetTaxWriteOffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AssetTaxWriteOffUseCase {

    private final AssetTaxWriteOffRepository repository;

    public AssetTaxWriteOffUseCase(AssetTaxWriteOffRepository repository) {
        this.repository = repository;
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
        return repository.save(writeOff);
    }

    @Transactional(readOnly = true)
    public List<AssetTaxWriteOff> getWriteOffHistory(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId);
    }
}
