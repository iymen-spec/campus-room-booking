package com.iymen.campusroombooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    private Long id;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "building")
    private String building;

    @Column(name = "capacity")
    private int capacity;

    public RoomEntity() {
    }

    public RoomEntity(Long id, String roomNumber, String building, int capacity) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public int getCapacity() {
        return capacity;
    }

}
