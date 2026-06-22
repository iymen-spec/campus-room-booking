package com.iymen.campusroombooking.service;

import com.iymen.campusroombooking.dto.BookingResponse;

public record BookingRescheduleResult(BookingRescheduleStatus status, BookingResponse bookingResponse) {
}
