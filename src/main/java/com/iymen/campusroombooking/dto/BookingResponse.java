package com.iymen.campusroombooking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingResponse(Long id, Long roomId, String bookedBy, LocalDate date, 
                            LocalTime startTime, LocalTime endTime) {}