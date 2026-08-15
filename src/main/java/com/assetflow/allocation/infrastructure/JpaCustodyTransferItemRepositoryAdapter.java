package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.CustodyTransferItem;
import com.assetflow.allocation.domain.CustodyTransferItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaCustodyTransferItemRepositoryAdapter implements CustodyTransferItemRepository {

    private final CustodyTransferItemJpaRepository jpaRepository;

    public JpaCustodyTransferItemRepositoryAdapter(CustodyTransferItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustodyTransferItem save(CustodyTransferItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public List<CustodyTransferItem> saveAll(List<CustodyTransferItem> items) {
        return jpaRepository.saveAll(items);
    }

    @Override
    public List<CustodyTransferItem> findByCustodyTransferId(UUID custodyTransferId) {
        return jpaRepository.findByCustodyTransferId(custodyTransferId);
    }
}
