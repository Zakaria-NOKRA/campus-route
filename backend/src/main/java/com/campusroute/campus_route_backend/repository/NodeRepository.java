package com.campusroute.campus_route_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusroute.campus_route_backend.domain.Node;

public interface NodeRepository extends JpaRepository<Node, String> {

    /** All the nodes of one plan space (building code, or EXT for the outdoor space). */
    List<Node> findByBuilding(String building);

    /** Floors of one plan space, sorted: they follow the data instead of being hard-coded. */
    @Query("select distinct n.floor from Node n where n.building = :building order by n.floor")
    List<Integer> findFloorsByBuilding(@Param("building") String building);
}
