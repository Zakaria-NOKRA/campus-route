package com.campusroute.campus_route_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusroute.campus_route_backend.domain.Edge;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    /**
     * Edges with both ends in one plan space. The links between a building door and the outdoor
     * space are left out: their two ends are not on the same plan.
     * "join fetch" loads both nodes in the same query (they are lazy).
     */
    @Query("""
            select e from Edge e join fetch e.a join fetch e.b
            where e.a.building = :building and e.b.building = :building
            """)
    List<Edge> findInsideBuilding(@Param("building") String building);
}
