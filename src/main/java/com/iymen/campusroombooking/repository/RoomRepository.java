package com.iymen.campusroombooking.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.iymen.campusroombooking.entity.RoomEntity;
import com.iymen.campusroombooking.model.Room;

@Repository
public class RoomRepository {

    private final RoomJpaRepository roomJpaRepository;

    public RoomRepository(RoomJpaRepository roomJpaRepository) {
        this.roomJpaRepository = roomJpaRepository;
    }

    public List<Room> findAll() {
        List<RoomEntity> roomEntities = roomJpaRepository.findAll();

        List<Room> rooms = new ArrayList<>();
        for (RoomEntity roomEntity : roomEntities) {
            rooms.add(toRoom(roomEntity));
        }
        return rooms;

    }

    private Room toRoom(RoomEntity roomEntity) {
        return new Room(
                roomEntity.getId(),
                roomEntity.getRoomNumber(),
                roomEntity.getBuilding(),
                roomEntity.getCapacity());
    }

    public Optional<Room> findById(Long id) {
        Optional<RoomEntity> roomEntity = roomJpaRepository.findById(id);
        if (roomEntity.isPresent()) {
            return Optional.of(toRoom(roomEntity.get()));
        }
        return Optional.empty();
    }
}
