package com.iymen.campusroombooking.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;


import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.model.Booking;

@Service
public class BookingService {
	
	
    public List<BookingResponse> findAllBookings(){
        List<BookingResponse> bookings = new ArrayList<>();
		for(Booking booking:hardcodedBookings()){
			bookings.add(toBookingResponse(booking));
		}
		return bookings;
	}
	
	private List<Booking> hardcodedBookings(){
		return List.of(
            new Booking(1L, 1L, "Alice", LocalDate.of(2026, 5, 29), LocalTime.of(10, 0), LocalTime.of(11, 0)),
            new Booking(2L, 2L, "Bob", LocalDate.of(2026, 6, 21), LocalTime.of(14, 0), LocalTime.of(15, 0)),
            new Booking(3L, 3L, "Charlie", LocalDate.of(2026, 7, 22), LocalTime.of(9, 0), LocalTime.of(10, 30))
        );
			
	}
	
	private BookingResponse toBookingResponse(Booking booking){
		return new BookingResponse(booking.id(),booking.roomId(),booking.bookedBy(),booking.date(),booking.startTime(),booking.endTime());
	}
	
}
