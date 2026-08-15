package com.assetflow.allocation.application;

import com.assetflow.allocation.domain.TemporaryAssetCheckout;
import com.assetflow.allocation.domain.TemporaryAssetCheckoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TemporaryAssetCheckoutUseCase {

    private final TemporaryAssetCheckoutRepository repository;

    public TemporaryAssetCheckoutUseCase(TemporaryAssetCheckoutRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TemporaryAssetCheckout checkoutAsset(
            UUID organizationId,
            UUID assetId,
            UUID borrowerEmployeeId,
            Instant expectedReturnTime,
            String checkoutConditionNotes) {
        repository.findActiveCheckoutByAssetId(organizationId, assetId).ifPresent(active -> {
            throw new IllegalStateException("Asset " + assetId + " is currently checked out to employee " + active.getBorrowerEmployeeId());
        });

        TemporaryAssetCheckout checkout = TemporaryAssetCheckout.checkout(
                organizationId,
                assetId,
                borrowerEmployeeId,
                expectedReturnTime,
                checkoutConditionNotes
        );
        return repository.save(checkout);
    }

    @Transactional
    public TemporaryAssetCheckout returnAsset(UUID checkoutId, String returnConditionNotes) {
        TemporaryAssetCheckout checkout = repository.findById(checkoutId)
                .orElseThrow(() -> new IllegalArgumentException("Checkout record not found: " + checkoutId));
        checkout.returnAsset(returnConditionNotes);
        return repository.save(checkout);
    }

    @Transactional(readOnly = true)
    public List<TemporaryAssetCheckout> getCheckoutsForBorrower(UUID organizationId, UUID borrowerEmployeeId) {
        return repository.findByOrganizationIdAndBorrowerEmployeeId(organizationId, borrowerEmployeeId);
    }
}
