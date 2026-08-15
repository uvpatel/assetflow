package com.assetflow.allocation.application;

import com.assetflow.allocation.api.dto.AcceptCustodyTransferRequest;
import com.assetflow.allocation.api.dto.CustodyTransferResponse;
import com.assetflow.allocation.api.dto.InitiateCustodyTransferRequest;
import com.assetflow.allocation.domain.CustodyTransfer;
import com.assetflow.allocation.domain.CustodyTransferItemRepository;
import com.assetflow.allocation.domain.CustodyTransferRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustodyTransferUseCaseTest {

    @Mock
    private CustodyTransferRepository transferRepository;

    @Mock
    private CustodyTransferItemRepository itemRepository;

    private CustodyTransferUseCase useCase;
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CustodyTransferUseCase(transferRepository, itemRepository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should successfully initiate multi-item custody transfer")
    void shouldInitiateMultiItemTransfer() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID assetId1 = UUID.randomUUID();
        UUID assetId2 = UUID.randomUUID();

        InitiateCustodyTransferRequest request = new InitiateCustodyTransferRequest(
                senderId,
                recipientId,
                List.of(assetId1, assetId2),
                "Office relocation handover"
        );

        when(transferRepository.save(any(CustodyTransfer.class))).thenAnswer(inv -> {
            CustodyTransfer t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        CustodyTransferResponse response = useCase.initiateTransfer(request);

        assertThat(response.senderEmployeeId()).isEqualTo(senderId);
        assertThat(response.recipientEmployeeId()).isEqualTo(recipientId);
        assertThat(response.items()).hasSize(2);
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Should accept custody transfer with digital signature")
    void shouldAcceptTransferWithSignature() {
        UUID transferId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        CustodyTransfer transfer = new CustodyTransfer(orgId, senderId, recipientId, "Laptops handover");
        transfer.setId(transferId);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any(CustodyTransfer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.findByCustodyTransferId(transferId)).thenReturn(List.of());

        AcceptCustodyTransferRequest request = new AcceptCustodyTransferRequest("data:image/png;base64,iVBORw0KGgo=");
        CustodyTransferResponse response = useCase.acceptTransfer(transferId, request);

        assertThat(response.status()).isEqualTo("ACCEPTED");
        assertThat(response.digitalSignature()).isEqualTo("data:image/png;base64,iVBORw0KGgo=");
        verify(transferRepository).save(transfer);
    }
}
