package com.iymen.campusroombooking.repository;

import com.iymen.campusroombooking.entity.RoomEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {

}
