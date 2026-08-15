package com.assetflow.allocation.api.dto;

import com.assetflow.allocation.domain.CustodyTransfer;
import com.assetflow.allocation.domain.CustodyTransferItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustodyTransferResponse(
        UUID id,
        UUID organizationId,
        UUID senderEmployeeId,
        UUID recipientEmployeeId,
        String status,
        String digitalSignature,
        Instant signedAt,
        String notes,
        List<CustodyTransferItemResponse> items
) {
    public record CustodyTransferItemResponse(
            UUID id,
            UUID assetId,
            String itemCondition,
            String notes
    ) {
        public static CustodyTransferItemResponse fromDomain(CustodyTransferItem item) {
            return new CustodyTransferItemResponse(item.getId(), item.getAssetId(), item.getItemCondition(), item.getNotes());
        }
    }

    public static CustodyTransferResponse fromDomain(CustodyTransfer transfer, List<CustodyTransferItem> items) {
        List<CustodyTransferItemResponse> itemDtos = items.stream()
                .map(CustodyTransferItemResponse::fromDomain)
                .toList();

        return new CustodyTransferResponse(
                transfer.getId(),
                transfer.getOrganizationId(),
                transfer.getSenderEmployeeId(),
                transfer.getRecipientEmployeeId(),
                transfer.getStatus().name(),
                transfer.getDigitalSignature(),
                transfer.getSignedAt(),
                transfer.getNotes(),
                itemDtos
        );
    }
}
