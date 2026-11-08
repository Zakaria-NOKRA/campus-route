package com.campusroute.campus_route_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusroute.campus_route_backend.domain.Edge;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
}
