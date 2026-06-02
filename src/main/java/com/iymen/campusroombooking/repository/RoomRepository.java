package com.iymen.campusroombooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.iymen.campusroombooking.model.Room;

@Repository
public class RoomRepository {

    private final List<Room> rooms = hardcodedRooms();

    public List<Room> findAll() {
        return rooms;
    }

    private List<Room> hardcodedRooms() {
        return List.of(
            new Room(1L, "101", "Science Hall", 30),
            new Room(2L, "202", "Library", 6),
            new Room(3L, "303", "Oren Gateway", 20)
        );
    }

    public Optional<Room> findById(Long id) {
        for (Room room : rooms) {
            if (room.id().equals(id)) {
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }
}
