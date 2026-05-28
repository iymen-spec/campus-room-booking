package com.iymen.campusroombooking.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Booking(Long id, Long roomId, String bookedBy, LocalDate date, 
                            LocalTime startTime, LocalTime endTime) {}
