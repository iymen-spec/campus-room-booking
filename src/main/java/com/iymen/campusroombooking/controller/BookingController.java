package com.iymen.campusroombooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
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
    public List<BookingResponse> bookings() {
        return bookingService.findAllBookings();
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
}
