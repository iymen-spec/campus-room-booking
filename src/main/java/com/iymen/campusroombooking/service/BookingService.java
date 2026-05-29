package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.Booking;

@Service
public class BookingService {

    private final List<Booking> bookings = new ArrayList<>();
    private final RoomService roomService;

    public BookingService(RoomService roomService) {
        this.roomService = roomService;
        loadHardcodedBookings();
    }

    public List<BookingResponse> findAllBookings() {
        List<BookingResponse> bookingsResp = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingsResp.add(toBookingResponse(booking));
        }

        return bookingsResp;
    }

    private void loadHardcodedBookings() {
        bookings.add(new Booking(1L, 1L, "Alice", LocalDate.of(2024, 6, 20), LocalTime.of(10, 0), LocalTime.of(11, 0)));
        bookings.add(new Booking(2L, 2L, "Bob", LocalDate.of(2024, 6, 20), LocalTime.of(9, 0), LocalTime.of(10, 0)));
        bookings.add(new Booking(3L, 1L, "Charlie", LocalDate.of(2024, 6, 21), LocalTime.of(14, 0), LocalTime.of(15, 0)));
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
            booking.id(),
            booking.roomId(),
            booking.bookedBy(),
            booking.date(),
            booking.startTime(),
            booking.endTime()
        );
    }

    public BookingCreationResult createBooking(BookingRequest request) {
        if (request.startTime().compareTo(request.endTime()) >= 0) {
            return new BookingCreationResult(BookingCreationStatus.INVALID_TIME, null);
        }

        if (roomService.findRoomById(request.roomId()).isEmpty()) {
            return new BookingCreationResult(BookingCreationStatus.ROOM_NOT_FOUND, null);
        }

        for (Booking booking : bookings) {
            boolean sameRoom = booking.roomId().equals(request.roomId());
            boolean sameDate = booking.date().equals(request.date());
            boolean overlaps = request.startTime().compareTo(booking.endTime()) < 0
                && request.endTime().compareTo(booking.startTime()) > 0;

            if (sameRoom && sameDate && overlaps) {
                return new BookingCreationResult(BookingCreationStatus.CONFLICT, null);
            }
        }

        Long id = bookings.size() + 1L;
        Booking booking = new Booking(
            id,
            request.roomId(),
            request.bookedBy(),
            request.date(),
            request.startTime(),
            request.endTime()
        );

        bookings.add(booking);

        return new BookingCreationResult(BookingCreationStatus.SUCCESS, toBookingResponse(booking));
    }
}
