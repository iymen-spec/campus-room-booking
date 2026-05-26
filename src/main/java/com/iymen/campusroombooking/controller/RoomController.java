package com.iymen.campusroombooking.controller;

import java.util.List;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.service.RoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

    private final RoomService roomService;
    
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    
    @GetMapping("/api/rooms")
    public List<RoomResponse> rooms() {
        return roomService.findAllRooms();
    }

    @GetMapping("/api/rooms/{id}")
    public ResponseEntity<RoomResponse> room(@PathVariable Long id) {
        RoomResponse room = roomService.findRoomById(id);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

    
