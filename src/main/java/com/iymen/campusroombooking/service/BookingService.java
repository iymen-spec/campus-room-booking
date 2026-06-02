package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.Booking;
import com.iymen.campusroombooking.model.BookingStatus;

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
        bookings.add(new Booking(1L, 1L, "Alice", LocalDate.of(2024, 6, 20), LocalTime.of(10, 0), LocalTime.of(11, 0), BookingStatus.ACTIVE));
        bookings.add(new Booking(2L, 2L, "Bob", LocalDate.of(2024, 6, 20), LocalTime.of(9, 0), LocalTime.of(10, 0), BookingStatus.ACTIVE));
        bookings.add(new Booking(3L, 1L, "Charlie", LocalDate.of(2024, 6, 21), LocalTime.of(14, 0), LocalTime.of(15, 0), BookingStatus.ACTIVE));
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
            booking.id(),
            booking.roomId(),
            booking.bookedBy(),
            booking.date(),
            booking.startTime(),
            booking.endTime(),
            booking.status()
        );
    }

    public BookingCreationResult createBooking(BookingRequest request) {
        if (request.startTime().compareTo(request.endTime()) >= 0) {
            return new BookingCreationResult(BookingCreationStatus.INVALID_TIME, null);
        }

        if (roomService.findRoomById(request.roomId()).isEmpty()) {
            return new BookingCreationResult(BookingCreationStatus.ROOM_NOT_FOUND, null);
        }

        if (hasConflict(request.roomId(), request.date(), request.startTime(), request.endTime())) {
            return new BookingCreationResult(BookingCreationStatus.CONFLICT, null);
        }

        Long id = bookings.size() + 1L;
        Booking booking = new Booking(
            id,
            request.roomId(),
            request.bookedBy(),
            request.date(),
            request.startTime(),
            request.endTime(),
            BookingStatus.ACTIVE
        );

        bookings.add(booking);

        return new BookingCreationResult(BookingCreationStatus.SUCCESS, toBookingResponse(booking));
    }

    public boolean hasConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (Booking booking : bookings) {
            if (booking.status() != BookingStatus.ACTIVE) {
                continue;
            }

            boolean sameRoom = booking.roomId().equals(roomId);
            boolean sameDate = booking.date().equals(date);
            boolean overlaps = startTime.compareTo(booking.endTime()) < 0
                && endTime.compareTo(booking.startTime()) > 0;

            if (sameRoom && sameDate && overlaps) {
                return true;
            }
        }

        return false;
    }

    public BookingCancellationStatus cancelBooking(Long id) {
        for (int i = 0; i < bookings.size(); ++i) {
            Booking booking = bookings.get(i);

            if (id.equals(booking.id())) {
                if (booking.status() == BookingStatus.CANCELED) {
                    return BookingCancellationStatus.CANCELED;
                }

                if (booking.status() == BookingStatus.ACTIVE) {
                    Booking canceledBooking = new Booking(
                        booking.id(),
                        booking.roomId(),
                        booking.bookedBy(),
                        booking.date(),
                        booking.startTime(),
                        booking.endTime(),
                        BookingStatus.CANCELED
                    );

                    bookings.set(i, canceledBooking);
                    return BookingCancellationStatus.CANCELED;
                }
            }
        }

        return BookingCancellationStatus.NOT_FOUND;
    }
}
