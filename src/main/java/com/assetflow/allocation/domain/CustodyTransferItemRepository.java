package com.assetflow.allocation.domain;

import java.util.List;
import java.util.UUID;

public interface CustodyTransferItemRepository {
    CustodyTransferItem save(CustodyTransferItem item);
    List<CustodyTransferItem> saveAll(List<CustodyTransferItem> items);
    List<CustodyTransferItem> findByCustodyTransferId(UUID custodyTransferId);
}
