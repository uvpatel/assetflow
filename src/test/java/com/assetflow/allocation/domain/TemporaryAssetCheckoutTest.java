package com.assetflow.allocation.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemporaryAssetCheckoutTest {

    @Test
    void shouldCheckoutAndReturnAssetCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        Instant expectedReturn = Instant.now().plus(4, ChronoUnit.HOURS);

        TemporaryAssetCheckout checkout = TemporaryAssetCheckout.checkout(
                orgId,
                assetId,
                borrowerId,
                expectedReturn,
                "Minor scratch on rear panel"
        );

        assertThat(checkout.getId()).isNotNull();
        assertThat(checkout.getStatus()).isEqualTo(TemporaryAssetCheckout.CheckoutStatus.CHECKED_OUT);
        assertThat(checkout.getActualReturnTime()).isNull();

        checkout.returnAsset("Returned in good working condition");

        assertThat(checkout.getStatus()).isEqualTo(TemporaryAssetCheckout.CheckoutStatus.RETURNED);
        assertThat(checkout.getActualReturnTime()).isNotNull();
        assertThat(checkout.getReturnConditionNotes()).isEqualTo("Returned in good working condition");
    }

    @Test
    void shouldRejectDuplicateReturnCall() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        Instant expectedReturn = Instant.now().plus(4, ChronoUnit.HOURS);

        TemporaryAssetCheckout checkout = TemporaryAssetCheckout.checkout(
                orgId,
                assetId,
                borrowerId,
                expectedReturn,
                "Clean checkout"
        );
        checkout.returnAsset("Returned");

        assertThatThrownBy(() -> checkout.returnAsset("Second return"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has already been returned");
    }
}
