package com.campusroute.campus_route_backend.dto;

import java.util.List;

import com.campusroute.campus_route_backend.domain.Room;

/** What the API returns for a room. Plan-drawing details (slot, top, node...) are left out on purpose. */
public record RoomDto(String code, String name, String type, int floor, int capacity, List<String> equipment) {

    public static RoomDto from(Room room) {
        return new RoomDto(room.getCode(), room.getName(), room.getType(), room.getFloor(),
                room.getCapacity(), List.copyOf(room.getEquipment()));
    }
}
