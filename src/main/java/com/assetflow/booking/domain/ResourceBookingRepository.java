package com.assetflow.booking.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourceBookingRepository {
    ResourceBooking save(ResourceBooking booking);
    Optional<ResourceBooking> findById(UUID id);
    List<ResourceBooking> findOverlappingBookings(UUID resourceId, Instant startTime, Instant endTime);
    List<ResourceBooking> findByOrganizationId(UUID organizationId);
}
