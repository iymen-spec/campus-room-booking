package com.iymen.campusroombooking.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.iymen.campusroombooking.dto.BookingResponse;
import com.iymen.campusroombooking.service.BookingService;

@RestController
public class BookingController {
    
	private final BookingService bookingService;
	
	public BookingController(BookingService bookingService){
			this.bookingService = bookingService;
	}
	
	@GetMapping("/api/bookings")
	public List<BookingResponse> bookings(){
		return bookingService.findAllBookings();
	}
}
