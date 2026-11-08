package com.campusroute.campus_route_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusroute.campus_route_backend.domain.Room;

public interface RoomRepository extends JpaRepository<Room, String> {
}
