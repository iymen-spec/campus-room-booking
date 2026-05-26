package com.iymen.campusroombooking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

    public record RoomResponse(Long id, String roomNumber, String building, int capacity) {}
    
    @GetMapping("/api/rooms")
    public List<RoomResponse> rooms() {
        
        return List.of(
            new RoomResponse(1L, "101", "Science Hall", 30),
            new RoomResponse(2L, "202", "Library", 6),
            new RoomResponse(3L, "303", "Oren Gateway", 20)
        );
    }

    @GetMapping("/api/rooms/{id}")
    public RoomResponse room(@PathVariable Long id) {
        for (RoomResponse room : rooms()) {
            if (room.id().equals(id)) {
                return room;
            }
        }
        return null;
    }
}

    
