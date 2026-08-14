package com.assetflow.booking.application;

import com.assetflow.booking.api.dto.CreateBookingRequest;
import com.assetflow.booking.domain.ResourceBooking;
import com.assetflow.booking.domain.ResourceBookingRepository;
import com.assetflow.shared.error.ConcurrencyConflictException;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingUseCase {

    private final ResourceBookingRepository bookingRepository;

    public BookingUseCase(ResourceBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ResourceBooking createBooking(CreateBookingRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new DomainViolationException("Booking end time must be after start time");
        }

        // Half-open interval overlap check: existing.start < requested.end AND existing.end > requested.start
        List<ResourceBooking> overlaps = bookingRepository.findOverlappingBookings(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new ConcurrencyConflictException("Requested time slot overlaps with an existing booking");
        }

        ResourceBooking booking = new ResourceBooking(
                organizationId,
                request.getResourceId(),
                request.getBookedByEmployeeId(),
                request.getTitle(),
                request.getStartTime(),
                request.getEndTime()
        );

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<ResourceBooking> listBookings() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return bookingRepository.findByOrganizationId(organizationId);
    }
}
