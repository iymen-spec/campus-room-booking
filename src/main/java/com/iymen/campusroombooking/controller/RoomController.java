package com.iymen.campusroombooking.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.service.AvailabilityResult;
import com.iymen.campusroombooking.service.AvailabilityService;
import com.iymen.campusroombooking.service.AvailabilityStatus;
import com.iymen.campusroombooking.service.RoomService;
import com.iymen.campusroombooking.dto.ErrorResponse;

@RestController
public class RoomController {

    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    public RoomController(RoomService roomService, AvailabilityService availabilityService) {
        this.roomService = roomService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/api/rooms")
    public ResponseEntity<?> rooms(
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Integer minCapacity) {
        if (minCapacity != null && minCapacity < 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("minCapacity cannot be negative."));
        }

        return ResponseEntity.ok(roomService.findRooms(building, minCapacity));
    }

    @GetMapping("/api/rooms/available")
    public ResponseEntity<?> findAvailableRooms(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Integer minCapacity) {
        if (minCapacity != null && minCapacity < 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("minCapacity cannot be negative."));
        }

        AvailabilityResult result = availabilityService.findAvailableRooms(
                date,
                startTime,
                endTime,
                building,
                minCapacity);

        if (result.status() == AvailabilityStatus.INVALID_TIME) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Start time must be before end time."));
        }

        return ResponseEntity.ok(result.availableRooms());
    }

    @GetMapping("/api/rooms/{id}")
    public ResponseEntity<?> room(@PathVariable Long id) {
        Optional<RoomResponse> room = roomService.findRoomById(id);

        if (room.isPresent()) {
            return ResponseEntity.ok(room.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Room not found."));
        }
    }
}
