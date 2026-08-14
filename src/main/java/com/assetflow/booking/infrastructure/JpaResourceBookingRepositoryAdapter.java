package com.assetflow.booking.infrastructure;

import com.assetflow.booking.domain.ResourceBooking;
import com.assetflow.booking.domain.ResourceBookingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataResourceBookingRepository extends JpaRepository<ResourceBooking, UUID> {
    List<ResourceBooking> findByOrganizationId(UUID organizationId);

    @Query("SELECT b FROM ResourceBooking b WHERE b.resourceId = :resourceId AND b.status = 'CONFIRMED' AND b.startTime < :endTime AND b.endTime > :startTime")
    List<ResourceBooking> findOverlappingBookings(UUID resourceId, Instant startTime, Instant endTime);
}

@Repository
public class JpaResourceBookingRepositoryAdapter implements ResourceBookingRepository {

    private final SpringDataResourceBookingRepository repository;

    public JpaResourceBookingRepositoryAdapter(SpringDataResourceBookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResourceBooking save(ResourceBooking booking) {
        return repository.save(booking);
    }

    @Override
    public Optional<ResourceBooking> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<ResourceBooking> findOverlappingBookings(UUID resourceId, Instant startTime, Instant endTime) {
        return repository.findOverlappingBookings(resourceId, startTime, endTime);
    }

    @Override
    public List<ResourceBooking> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId);
    }
}
