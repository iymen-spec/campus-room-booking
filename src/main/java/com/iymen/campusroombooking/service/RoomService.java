package com.iymen.campusroombooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.model.Room;

@Service
public class RoomService {

    public List<RoomResponse> findRooms(String building, Integer minCapacity) {
        List<RoomResponse> roomResponses = new ArrayList<>();
		
        for(Room room: hardcodedRooms()){
			boolean matchesBuilding = building==null||room.building().equalsIgnoreCase(building);
			boolean matchesCapacity = minCapacity==null||room.capacity()>=minCapacity;
			
			if( matchesBuilding && matchesCapacity){
				roomResponses.add(toRoomResponse(room));
			}
		}
		return roomResponses;
    }
    
	private List<Room> hardcodedRooms(){
		return List.of(
            new Room(1L, "101", "Science Hall", 30),
            new Room(2L, "202", "Library", 6),
            new Room(3L, "303", "Oren Gateway", 20)
        );
	}
    public Optional<RoomResponse> findRoomById(Long id) {
        for (Room room : hardcodedRooms()) {
            if (room.id().equals(id)) {
                return Optional.of(toRoomResponse(room));
            }
        }
        return Optional.empty();
    }
	private RoomResponse toRoomResponse(Room room){
		return new RoomResponse(room.id(),room.roomNumber(),room.building(),room.capacity());
	}
}
