package com.iymen.campusroombooking.controller;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.BookingStatus;
import com.iymen.campusroombooking.service.BookingCancellationStatus;
import com.iymen.campusroombooking.service.BookingCreationResult;
import com.iymen.campusroombooking.service.BookingCreationStatus;
import com.iymen.campusroombooking.service.BookingService;
import com.iymen.campusroombooking.dto.ErrorResponse;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/api/bookings")
    public List<BookingResponse> bookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) LocalDate date) {
        return bookingService.findBookings(status, roomId, date);
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<?> booking(@PathVariable Long id) {
        Optional<BookingResponse> booking = bookingService.findBookingById(id);

        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Booking not found."));
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingCreationResult result = bookingService.createBooking(request);

        if (result.status() == BookingCreationStatus.SUCCESS) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result.bookingResponse());
        }

        if (result.status() == BookingCreationStatus.INVALID_TIME) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Start time must be before end time."));
        }

        if (result.status() == BookingCreationStatus.ROOM_NOT_FOUND) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Room not found."));
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Room is already booked for that time."));
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        BookingCancellationStatus result = bookingService.cancelBooking(id);

        if (result == BookingCancellationStatus.NOT_FOUND) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Booking not found."));
        }

        return ResponseEntity
                .noContent()
                .build();
    }
}
