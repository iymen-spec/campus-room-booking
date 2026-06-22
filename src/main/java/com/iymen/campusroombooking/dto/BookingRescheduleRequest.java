package com.iymen.campusroombooking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record BookingRescheduleRequest(
        @NotNull Long roomId,
        @NotNull LocalDate date,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime) {
}
