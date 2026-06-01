package com.iymen.campusroombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.RoomResponse;

@Service
public class AvailabilityService {

    private final BookingService bookingService;
    private final RoomService roomService;

    public AvailabilityService(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    public AvailabilityResult findAvailableRooms(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String building,
            Integer minCapacity) {
        if (startTime.compareTo(endTime) >= 0) {
            return new AvailabilityResult(AvailabilityStatus.INVALID_TIME, null);
        }

        List<RoomResponse> filteredRooms = roomService.findRooms(building, minCapacity);
        List<RoomResponse> availableRooms = new ArrayList<>();

        for (RoomResponse room : filteredRooms) {
            if (!bookingService.hasConflict(room.id(), date, startTime, endTime)) {
                availableRooms.add(room);
            }
        }

        return new AvailabilityResult(AvailabilityStatus.SUCCESS, availableRooms);
    }
}