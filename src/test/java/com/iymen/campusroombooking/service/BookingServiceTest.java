package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.repository.RoomRepository;
import com.iymen.campusroombooking.repository.BookingRepository;
import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.model.BookingStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @Test
    public void createBooking_withValidRequest_returnsSuccess() {

        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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

        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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

        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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
        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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
        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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

        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RoomService roomService = new RoomService(roomRepository);
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
