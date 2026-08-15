package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.CustodyTransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustodyTransferItemJpaRepository extends JpaRepository<CustodyTransferItem, UUID> {
    List<CustodyTransferItem> findByCustodyTransferId(UUID custodyTransferId);
}
