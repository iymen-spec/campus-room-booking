package com.iymen.campusroombooking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.BookingStatus;
import com.iymen.campusroombooking.service.BookingCancellationStatus;
import com.iymen.campusroombooking.service.BookingCreationResult;
import com.iymen.campusroombooking.service.BookingCreationStatus;
import com.iymen.campusroombooking.service.BookingService;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/api/bookings")
    public List<BookingResponse> bookings(@RequestParam(required = false) BookingStatus status) {
        return bookingService.findBookings(status);
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<BookingResponse> booking(@PathVariable Long id) {
        Optional<BookingResponse> booking = bookingService.findBookingById(id);

        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingCreationResult result = bookingService.createBooking(request);

        if (result.status() == BookingCreationStatus.SUCCESS) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.bookingResponse());
        }

        if (result.status() == BookingCreationStatus.INVALID_TIME) {
            return ResponseEntity.badRequest().build();
        }

        if (result.status() == BookingCreationStatus.ROOM_NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        BookingCancellationStatus result = bookingService.cancelBooking(id);

        if (result == BookingCancellationStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
