package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumableBatchRepository {
    ConsumableBatch save(ConsumableBatch batch);
    Optional<ConsumableBatch> findById(UUID id);
    List<ConsumableBatch> findByAssetId(UUID assetId);
    List<ConsumableBatch> findByOrganizationId(UUID organizationId);
}
