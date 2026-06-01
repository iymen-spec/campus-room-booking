package com.iymen.campusroombooking.service;

import java.util.List;

import com.iymen.campusroombooking.dto.RoomResponse;

public record AvailabilityResult(AvailabilityStatus status, List<RoomResponse> availableRooms) {
}