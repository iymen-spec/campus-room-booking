package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.Booking;
import com.iymen.campusroombooking.model.BookingStatus;
import com.iymen.campusroombooking.repository.BookingRepository;

@Service
public class BookingService {

    private final RoomService roomService;
    private final BookingRepository bookingRepository;

    public BookingService(RoomService roomService, BookingRepository bookingRepository) {
        this.roomService = roomService;
        this.bookingRepository = bookingRepository;
    }

    public List<BookingResponse> findBookings(BookingStatus status, Long roomId, LocalDate date) {
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookingRepository.findAll()) {
            boolean matchingStatus = status == null || booking.status() == status;
            boolean matchingRoomId = roomId == null || booking.roomId().equals(roomId);
            boolean matchingDate = date == null || booking.date().equals(date);

            if (matchingStatus && matchingRoomId && matchingDate) {
                responses.add(toBookingResponse(booking));
            }
        }

        return responses;
    }

    public Optional<BookingResponse> findBookingById(Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);

        if (bookingOpt.isPresent()) {
            return Optional.of(toBookingResponse(bookingOpt.get()));
        }

        return Optional.empty();
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.id(),
                booking.roomId(),
                booking.bookedBy(),
                booking.date(),
                booking.startTime(),
                booking.endTime(),
                booking.status());
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

        Booking booking = new Booking(
                null,
                request.roomId(),
                request.bookedBy(),
                request.date(),
                request.startTime(),
                request.endTime(),
                BookingStatus.ACTIVE);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingCreationResult(BookingCreationStatus.SUCCESS, toBookingResponse(savedBooking));
    }

    public boolean hasConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (Booking booking : bookingRepository.findAll()) {
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
        Optional<Booking> bookingOpt = bookingRepository.findById(id);

        if (bookingOpt.isEmpty()) {
            return BookingCancellationStatus.NOT_FOUND;
        }

        Booking booking = bookingOpt.get();

        if (booking.status() == BookingStatus.CANCELED) {
            return BookingCancellationStatus.CANCELED;
        }

        Booking canceledBooking = new Booking(
                booking.id(),
                booking.roomId(),
                booking.bookedBy(),
                booking.date(),
                booking.startTime(),
                booking.endTime(),
                BookingStatus.CANCELED);

        boolean replaced = bookingRepository.replace(canceledBooking);

        if (!replaced) {
            return BookingCancellationStatus.NOT_FOUND;
        }

        return BookingCancellationStatus.CANCELED;
    }
}
