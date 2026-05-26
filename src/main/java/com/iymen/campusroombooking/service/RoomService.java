package com.iymen.campusroombooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.RoomResponse;

@Service
public class RoomService {

    public List<RoomResponse> findAllRooms() {
        return List.of(
            new RoomResponse(1L, "101", "Science Hall", 30),
            new RoomResponse(2L, "202", "Library", 6),
            new RoomResponse(3L, "303", "Oren Gateway", 20)
        );
    }
    
    public RoomResponse findRoomById(Long id) {
        for (RoomResponse room : findAllRooms()) {
            if (room.id().equals(id)) {
                return room;
            }
        }
        return null;
    }
}
