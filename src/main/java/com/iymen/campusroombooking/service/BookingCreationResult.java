package com.iymen.campusroombooking.service;

import com.iymen.campusroombooking.dto.BookingResponse;


public record BookingCreationResult(
    BookingCreationStatus status, BookingResponse bookingResponse
) {}
