package com.assetflow.booking.api;

import com.assetflow.booking.api.dto.CreateBookingRequest;
import com.assetflow.booking.application.BookingUseCase;
import com.assetflow.booking.domain.ResourceBooking;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingUseCase bookingUseCase;

    public BookingController(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @PostMapping
    public ResponseEntity<ResourceBooking> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        ResourceBooking booking = bookingUseCase.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping
    public ResponseEntity<List<ResourceBooking>> listBookings() {
        return ResponseEntity.ok(bookingUseCase.listBookings());
    }
}
