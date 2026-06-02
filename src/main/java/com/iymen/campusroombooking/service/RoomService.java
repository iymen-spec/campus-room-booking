package com.iymen.campusroombooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iymen.campusroombooking.dto.RoomResponse;
import com.iymen.campusroombooking.model.Room;
import com.iymen.campusroombooking.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomResponse> findRooms(String building, Integer minCapacity) {
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : roomRepository.findAll()) {
            boolean matchesBuilding = building == null || room.building().equalsIgnoreCase(building);
            boolean matchesCapacity = minCapacity == null || room.capacity() >= minCapacity;

            if (matchesBuilding && matchesCapacity) {
                roomResponses.add(toRoomResponse(room));
            }
        }

        return roomResponses;
    }

    public Optional<RoomResponse> findRoomById(Long id) {
        Optional<Room> roomOpt = roomRepository.findById(id);

        if (roomOpt.isPresent()) {
            return Optional.of(toRoomResponse(roomOpt.get()));
        }

        return Optional.empty();
    }

    private RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
            room.id(),
            room.roomNumber(),
            room.building(),
            room.capacity()
        );
    }
}
