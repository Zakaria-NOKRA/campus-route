package com.campusroute.campus_route_backend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusroute.campus_route_backend.domain.Room;

public interface RoomRepository extends JpaRepository<Room, String> {

    /** Rooms of one building, sorted by code. */
    List<Room> findByBuildingOrderByCode(String building);

    /**
     * Rooms whose code, name or type contains the pattern, ignoring case and spaces (like the prototype).
     * The pattern must already be lower-case, without spaces, with the LIKE wildcards escaped by '\'.
     * The Pageable only serves as a LIMIT: the result is sorted by code before it is cut.
     */
    @Query("""
            select r from Room r
            where lower(r.code) like :pattern escape '\\'
               or lower(replace(r.name, ' ', '')) like :pattern escape '\\'
               or lower(replace(r.type, ' ', '')) like :pattern escape '\\'
            order by r.code
            """)
    List<Room> search(@Param("pattern") String pattern, Pageable limit);
}
