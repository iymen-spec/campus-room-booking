package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.model.Booking;
import com.iymen.campusroombooking.repository.BookingRepository;
import com.iymen.campusroombooking.dto.BookingRequest;
import com.iymen.campusroombooking.dto.BookingRescheduleRequest;
import com.iymen.campusroombooking.model.BookingStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

        @Test
        public void createBooking_withValidRequest_returnsSuccess() {

                BookingRepository bookingRepository = mock(BookingRepository.class);

                // create fake roomserverice for same reason.
                RoomService roomService = mock(RoomService.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRequest bookingRequest = new BookingRequest(
                                1L, "John Doe", LocalDate.of(2026, 6, 15),
                                LocalTime.of(10, 0), LocalTime.of(11, 0));

                // createBooking calls this method to very if a room exists so we let the fake
                // know that it should send a room.
                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));

                when(bookingRepository.findAll()).thenReturn(List.of());

                when(bookingRepository.save(any(Booking.class)))
                                .thenReturn(new Booking(
                                                5L,
                                                1L,
                                                "John Doe",
                                                LocalDate.of(2026, 6, 15),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE));

                BookingCreationResult result = bookingService.createBooking(bookingRequest);

                assertEquals(BookingCreationStatus.SUCCESS, result.status());
                assertNotNull(result.bookingResponse());
                assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
                assertEquals(5L, result.bookingResponse().id());
        }

        @Test
        public void createBooking_withInvalidTime_returnsInvalidTime() {

                RoomService roomService = mock(RoomService.class);

                BookingRepository bookingRepository = mock(BookingRepository.class);
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

                BookingRepository bookingRepository = mock(BookingRepository.class);

                RoomService roomService = mock(RoomService.class);
                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRequest request = new BookingRequest(
                                1L, "John Doe", LocalDate.of(2026, 6, 20),
                                LocalTime.of(10, 00), LocalTime.of(11, 00));

                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));

                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(2L, 1L, "John Doe", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 30), LocalTime.of(11, 30), BookingStatus.ACTIVE)));

                BookingCreationResult result = bookingService.createBooking(request);

                assertEquals(BookingCreationStatus.CONFLICT, result.status());

                assertNull(result.bookingResponse());
        }

        @Test
        public void createBooking_withBackToBackBooking_returnsSuccess() {

                BookingRepository bookingRepository = mock(BookingRepository.class);

                RoomService roomService = mock(RoomService.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRequest request = new BookingRequest(
                                1L, "John Doe", LocalDate.of(2026, 6, 20),
                                LocalTime.of(11, 0), LocalTime.of(12, 0));

                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));

                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(2L, 1L, "John Doe", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 30), LocalTime.of(11, 00), BookingStatus.ACTIVE)));

                when(bookingRepository.save(any(Booking.class)))
                                .thenReturn(new Booking(
                                                4L,
                                                1L,
                                                "Iymen",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(11, 0),
                                                LocalTime.of(12, 0),
                                                BookingStatus.ACTIVE));
                BookingCreationResult result = bookingService.createBooking(request);

                assertEquals(BookingCreationStatus.SUCCESS, result.status());
                assertNotNull(result.bookingResponse());
                assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
        }

        @Test
        public void createBooking_afterCancelingConflictingBooking_returnsSuccess() {

                BookingRepository bookingRepository = mock(BookingRepository.class);
                RoomService roomService = mock(RoomService.class);
                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRequest request = new BookingRequest(
                                1L, "John Doe", LocalDate.of(2026, 6, 20),
                                LocalTime.of(10, 0), LocalTime.of(11, 0));

                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Iymen",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));
                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(1L, 1L, "Iymen", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.CANCELED)));
                when(bookingRepository.save(any(Booking.class)))
                                .thenReturn(new Booking(
                                                5L,
                                                1L,
                                                "John Doe",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE));
                when(bookingRepository.replace(any(Booking.class))).thenReturn(true);

                BookingCancellationStatus cancelResult = bookingService.cancelBooking(1L);
                assertEquals(BookingCancellationStatus.CANCELED, cancelResult);

                BookingCreationResult result = bookingService.createBooking(request);

                assertEquals(BookingCreationStatus.SUCCESS, result.status());
                assertNotNull(result.bookingResponse());
                assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
        }

        @Test
        public void findAvailableRooms_afterCancelingConflictingBooking_includesRoom() {

                BookingRepository bookingRepository = mock(BookingRepository.class);
                RoomService roomService = mock(RoomService.class);
                BookingService bookingService = new BookingService(roomService, bookingRepository);
                AvailabilityService availabilityService = new AvailabilityService(bookingService, roomService);

                when(roomService.findRooms(null, null))
                                .thenReturn(List.of(
                                                new RoomResponse(1L, "101", "Science Hall", 30),
                                                new RoomResponse(2L, "202", "Library", 6),
                                                new RoomResponse(3L, "303", "Oren Gateway", 20)));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Iymen",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));

                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(
                                                1L,
                                                1L,
                                                "Iymen",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.CANCELED)));

                when(bookingRepository.replace(any(Booking.class))).thenReturn(true);

                BookingCancellationStatus canceled = bookingService.cancelBooking(1L);
                assertEquals(BookingCancellationStatus.CANCELED, canceled);
                AvailabilityResult result = availabilityService.findAvailableRooms(LocalDate.of(2026, 6, 20),
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

        @Test
        public void rescheduleBooking_whenNewTimeOverlapsSameBooking_returnsSuccess() {

                RoomService roomService = mock(RoomService.class);
                BookingRepository bookingRepository = mock(BookingRepository.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(10, 30),
                                LocalTime.of(11, 30));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));
                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(1L, 1L, "Alice", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));
                when(bookingRepository.replace(any(Booking.class))).thenReturn(true);

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.SUCCESS, result.status());
                assertNotNull(result.bookingResponse());
                assertEquals(1L, result.bookingResponse().id());
                assertEquals("Alice", result.bookingResponse().bookedBy());
                assertEquals(BookingStatus.ACTIVE, result.bookingResponse().status());
                assertEquals(LocalTime.of(10, 30), result.bookingResponse().startTime());
                assertEquals(LocalTime.of(11, 30), result.bookingResponse().endTime());
                assertEquals(1L, result.bookingResponse().roomId());
                assertEquals(LocalDate.of(2026, 6, 20), result.bookingResponse().date());

        }

        @Test
        public void rescheduleBooking_whenNewTimeOverlapsAnotherActiveBooking_returnsConflict() {
                RoomService roomService = mock(RoomService.class);
                BookingRepository bookingRepository = mock(BookingRepository.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(10, 30),
                                LocalTime.of(11, 30));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));
                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(1L, 1L, "Alice", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE),
                                                new Booking(2L, 1L, "Bob", LocalDate.of(2026, 6, 20),
                                                                LocalTime.of(10, 45), LocalTime.of(11, 45),
                                                                BookingStatus.ACTIVE)));

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.CONFLICT, result.status());
                assertNull(result.bookingResponse());

        }

        @Test
        public void rescheduleBooking_withMissingBooking_returnsBookingNotFound() {
                RoomService roomService = mock(RoomService.class);
                BookingRepository bookingRepository = mock(BookingRepository.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(10, 30),
                                LocalTime.of(11, 30));

                when(bookingRepository.findById(99L))
                                .thenReturn(Optional.empty());

                BookingRescheduleResult result = bookingService.rescheduleBooking(99L, request);

                assertEquals(BookingRescheduleStatus.BOOKING_NOT_FOUND, result.status());
                assertNull(result.bookingResponse());
        }

        @Test
        public void rescheduleBooking_withCanceledBooking_returnsCanceledBooking() {
                RoomService roomService = mock(RoomService.class);
                BookingRepository bookingRepository = mock(BookingRepository.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(10, 30),
                                LocalTime.of(11, 30));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.CANCELED)));

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.CANCELED_BOOKING, result.status());
                assertNull(result.bookingResponse());
        }

        @Test
        public void rescheduleBooking_withInvalidTime_returnsInvalidTime() {

                RoomService roomService = mock(RoomService.class);

                BookingRepository bookingRepository = mock(BookingRepository.class);
                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(11, 30),
                                LocalTime.of(10, 30));
                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.INVALID_TIME, result.status());
                assertNull(result.bookingResponse());
        }

        @Test
        public void rescheduleBooking_withMissingRoom_returnsRoomNotFound() {

                RoomService roomService = mock(RoomService.class);

                BookingRepository bookingRepository = mock(BookingRepository.class);
                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(11, 30),
                                LocalTime.of(12, 30));
                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(10, 0),
                                                LocalTime.of(11, 0),
                                                BookingStatus.ACTIVE)));
                when(roomService.findRoomById(request.roomId())).thenReturn(Optional.empty());

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.ROOM_NOT_FOUND, result.status());
                assertNull(result.bookingResponse());
        }

        @Test
        public void rescheduleBooking_withBackToBackTime_returnsSuccess() {

                RoomService roomService = mock(RoomService.class);
                BookingRepository bookingRepository = mock(BookingRepository.class);

                BookingService bookingService = new BookingService(roomService, bookingRepository);

                BookingRescheduleRequest request = new BookingRescheduleRequest(1L,
                                LocalDate.of(2026, 6, 20), LocalTime.of(11, 00),
                                LocalTime.of(12, 00));

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(new Booking(
                                                1L,
                                                1L,
                                                "Alice",
                                                LocalDate.of(2026, 6, 20),
                                                LocalTime.of(9, 00),
                                                LocalTime.of(9, 30),
                                                BookingStatus.ACTIVE)));
                when(roomService.findRoomById(1L))
                                .thenReturn(Optional.of(new RoomResponse(1L, "101", "Science Hall", 30)));
                when(bookingRepository.findAll())
                                .thenReturn(List.of(new Booking(1L, 1L, "Alice", LocalDate.of(2026, 6, 20),
                                                LocalTime.of(9, 00),
                                                LocalTime.of(9, 30),
                                                BookingStatus.ACTIVE),
                                                new Booking(2L, 1L, "Bob", LocalDate.of(2026, 6, 20),
                                                                LocalTime.of(10, 00), LocalTime.of(11, 00),
                                                                BookingStatus.ACTIVE)));
                when(bookingRepository.replace(any(Booking.class))).thenReturn(true);

                BookingRescheduleResult result = bookingService.rescheduleBooking(1L, request);

                assertEquals(BookingRescheduleStatus.SUCCESS, result.status());
                assertNotNull(result.bookingResponse());
                assertEquals(1L, result.bookingResponse().id());
                assertEquals(LocalTime.of(11, 00), result.bookingResponse().startTime());
                assertEquals(LocalTime.of(12, 00), result.bookingResponse().endTime());

        }

}
