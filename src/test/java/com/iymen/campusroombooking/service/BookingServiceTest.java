package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.repository.BookingRepository;

import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.model.BookingStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    @Test
    public void createBooking_withValidRequest_returnsSuccess() {

        BookingRepository bookingRepository = new BookingRepository();

        RoomService roomService = mock(RoomService.class);

        when(roomService.findRoomById(1L))
                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));

        BookingService bookingService = new BookingService(roomService, bookingRepository);

        BookingRequest bookingRequest = new BookingRequest(
                1L, "John Doe", LocalDate.of(2024, 6, 15),
                LocalTime.of(10, 0), LocalTime.of(11, 0));

        BookingCreationResult result = bookingService.createBooking(bookingRequest);

        assertEquals(BookingCreationStatus.SUCCESS, result.status());
        assertNotNull(result.bookingResponse());
        assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
    }

    @Test
    public void createBooking_withInvalidTime_returnsInvalidTime() {

        RoomService roomService = mock(RoomService.class);
        BookingRepository bookingRepository = new BookingRepository();
        BookingService bookingService = new BookingService(roomService, bookingRepository);

        BookingRequest bookingRequest = new BookingRequest(
                1L, "John Doe", LocalDate.of(2026, 6, 4),
                LocalTime.of(11, 0), LocalTime.of(10, 0));

        BookingCreationResult result = bookingService.createBooking(bookingRequest);

        assertEquals(BookingCreationStatus.INVALID_TIME, result.status());
        assertNull(result.bookingResponse());

    }

    @Test
    public void createBooking_withOverlappingActiveBooking_returnsConflict() {

        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = mock(RoomService.class);
        when(roomService.findRoomById(1L))
                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
        BookingService bookingService = new BookingService(roomService, bookingRepository);

        BookingRequest request = new BookingRequest(
                1L, "John Doe", LocalDate.of(2024, 6, 20),
                LocalTime.of(10, 30), LocalTime.of(11, 30));

        BookingCreationResult result = bookingService.createBooking(request);

        assertEquals(BookingCreationStatus.CONFLICT, result.status());

        assertNull(result.bookingResponse());
    }

    @Test
    public void createBooking_withBackToBackBooking_returnsSuccess() {

        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = mock(RoomService.class);
        when(roomService.findRoomById(1L))
                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
        BookingService bookingService = new BookingService(roomService, bookingRepository);

        BookingRequest request = new BookingRequest(
                1L, "John Doe", LocalDate.of(2024, 6, 20),
                LocalTime.of(11, 0), LocalTime.of(12, 0));

        BookingCreationResult result = bookingService.createBooking(request);

        assertEquals(BookingCreationStatus.SUCCESS, result.status());
        assertNotNull(result.bookingResponse());
        assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
    }

    @Test
    public void createBooking_afterCancelingConflictingBooking_returnsSuccess() {

        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = mock(RoomService.class);
        when(roomService.findRoomById(1L))
                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
        BookingService bookingService = new BookingService(roomService, bookingRepository);

        BookingCancellationStatus cancelResult = bookingService.cancelBooking(1L);
        assertEquals(BookingCancellationStatus.CANCELED, cancelResult);

        BookingRequest request = new BookingRequest(
                1L, "John Doe", LocalDate.of(2024, 6, 20),
                LocalTime.of(10, 0), LocalTime.of(11, 0));

        BookingCreationResult result = bookingService.createBooking(request);

        assertEquals(BookingCreationStatus.SUCCESS, result.status());
        assertNotNull(result.bookingResponse());
        assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
    }

    @Test
    public void findAvailableRooms_afterCancelingConflictingBooking_includesRoom() {

        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = mock(RoomService.class);
        when(roomService.findRooms(null, null))
                .thenReturn(List.of(
                        new RoomResponse(1L, "101", "Science Hall", 30),
                        new RoomResponse(2L, "202", "Library", 6),
                        new RoomResponse(3L, "303", "Oren Gateway", 20)));
        BookingService bookingService = new BookingService(roomService, bookingRepository);
        AvailabilityService availabilityService = new AvailabilityService(bookingService, roomService);
        BookingCancellationStatus canceled = bookingService.cancelBooking(1L);
        assertEquals(BookingCancellationStatus.CANCELED, canceled);
        AvailabilityResult result = availabilityService.findAvailableRooms(LocalDate.of(2024, 6, 20),
                LocalTime.of(10, 0), LocalTime.of(11, 0), null, null);

        assertEquals(AvailabilityStatus.SUCCESS, result.status());

        boolean foundRoomOne = false;
        for (RoomResponse room : result.availableRooms()) {
            if (room.id().equals(1L)) {
                foundRoomOne = true;
            }
        }
        assertTrue(foundRoomOne);

    }
}
