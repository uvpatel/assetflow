package com.assetflow.reporting.application;

import com.assetflow.asset.domain.Asset;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CsvExportService {

    private final AssetRepository assetRepository;

    public CsvExportService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public String exportAssetsCsv() {
        UUID organizationId = TenantContext.requireOrganizationId();
        List<Asset> assets = assetRepository.findByOrganizationId(organizationId);

        StringBuilder sb = new StringBuilder();
        sb.append("ID,AssetTag,Name,Status,ConditionState,Location\n");

        for (Asset a : assets) {
            sb.append(String.format("%s,%s,\"%s\",%s,%s,\"%s\"\n",
                    a.getId(),
                    a.getAssetTag(),
                    a.getName().replace("\"", "\"\""),
                    a.getStatus(),
                    a.getConditionState(),
                    a.getLocation() != null ? a.getLocation().replace("\"", "\"\"") : ""
            ));
        }

        return sb.toString();
    }
}
