package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SoftwareLicenseTest {

    @Test
    void shouldAllocateAndRevokeSeatsCorrectly() {
        UUID orgId = UUID.randomUUID();

        SoftwareLicense license = SoftwareLicense.create(
                orgId,
                "IntelliJ IDEA Ultimate",
                "KEY-IJ-98765",
                2,
                LocalDate.now().plusYears(1)
        );

        assertThat(license.getId()).isNotNull();
        assertThat(license.getTotalSeats()).isEqualTo(2);
        assertThat(license.getAllocatedSeats()).isEqualTo(0);
        assertThat(license.getAvailableSeats()).isEqualTo(2);

        license.allocateSeat();
        assertThat(license.getAllocatedSeats()).isEqualTo(1);
        assertThat(license.getAvailableSeats()).isEqualTo(1);

        license.allocateSeat();
        assertThat(license.getAllocatedSeats()).isEqualTo(2);
        assertThat(license.getAvailableSeats()).isEqualTo(0);

        license.revokeSeat();
        assertThat(license.getAllocatedSeats()).isEqualTo(1);
        assertThat(license.getAvailableSeats()).isEqualTo(1);
    }

    @Test
    void shouldRejectSeatOverAllocation() {
        UUID orgId = UUID.randomUUID();
        SoftwareLicense license = SoftwareLicense.create(orgId, "Figma Pro", "KEY-FG-11", 1, null);
        license.allocateSeat();

        assertThatThrownBy(license::allocateSeat)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("All software license seats are currently allocated");
    }

    @Test
    void shouldRejectRevokingUnallocatedSeat() {
        UUID orgId = UUID.randomUUID();
        SoftwareLicense license = SoftwareLicense.create(orgId, "Slack Enterprise", "KEY-SL-22", 5, null);

        assertThatThrownBy(license::revokeSeat)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No allocated seats available to revoke");
    }
}
